package com.ianhattendorf.sensiapi;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ianhattendorf.sensiapi.exception.APIException;
import com.ianhattendorf.sensiapi.request.AuthorizeRequest;
import com.ianhattendorf.sensiapi.request.SubscribeRequest;
import com.ianhattendorf.sensiapi.response.*;
import com.ianhattendorf.sensiapi.response.data.OperationalStatus;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SensiApi {
    private final RetrofitApi retrofitApi;
    private final String username;
    private final String password;
    private int subscriptionId = 0;
    private String connectionToken;
    private String groupsToken;
    private String messageId;
    private List<ThermostatResponse> thermostats;
    private Map<String, OperationalStatus> operationalStatuses = new HashMap<>();
    private final Set<Consumer<OperationalStatus>> callbacks = new LinkedHashSet<>();
    private final Gson gson = gsonFactory();

    private static final Logger logger = LoggerFactory.getLogger(SensiApi.class);
    private static final String TRANSPORT = "longPolling";
    // [{"name":"thermostat-v1"}]
    private static final String CONNECTION_DATA = "%5B%7B%22name%22%3A%22thermostat-v1%22%7D%5D";
    private static final String OPERATIONAL_STATUS_ICD_PATH = "$.M[0].A[0]";
    private static final String OPERATIONAL_STATUS_DATA_PATH = "$.M[0].A[1].OperationalStatus";
    private static final String POLL_C_PATH = "$.C";
    private static final String POLL_G_PATH = "$.G";
    public static final Interceptor DEFAULT_OK_HTTP_INTERCEPTOR = chain -> {
        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
                .header("Accept", "application/json; version=1, */*; q=0.01")
                .header("Connection", "keep-alive")
                .header("Origin", "https://mythermostat.sensicomfort.com")
                .header("Referer", "https://mythermostat.sensicomfort.com/")
                .header("User-Agent", "Mozilla/5.0 (X11; Fedora; Linux x86_64) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.96 Safari/537.36")
                .method(original.method(), original.body());

        boolean isRealtime = original.url().encodedPath().startsWith("/realtime");
        if (isRealtime) {
            requestBuilder = requestBuilder
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        } else {
            requestBuilder = requestBuilder
                    .header("X-Requested-With", "XMLHttpRequest");
        }

        return chain.proceed(requestBuilder.build());
    };

    static {
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new GsonJsonProvider(gsonFactory());
            private final MappingProvider mappingProvider = new GsonMappingProvider(gsonFactory());

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.of(Option.DEFAULT_PATH_LEAF_TO_NULL);
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }
        });
    }

    public SensiApi(String username, String password, RetrofitApi retrofitApi) {
        this.username = username;
        this.password = password;
        this.retrofitApi = retrofitApi;
    }

    public void start() throws IOException, APIException {
        // authorize (get auth cookie)
        Response<AuthorizeResponse> authResponse = retrofitApi.authorize(
                new AuthorizeRequest(username, password)
        ).execute();
        verifyResponse(authResponse);

        // get thermostats
        Response<List<ThermostatResponse>> thermResponse = retrofitApi.thermostats().execute();
        verifyResponse(thermResponse);
        thermostats = thermResponse.body();

        // realtime negotiate (get connectionToken)
        Response<NegotiateResponse> negotiateResponse = retrofitApi.negotiate(SensiApi.getUnixTimestamp()).execute();
        verifyResponse(negotiateResponse);
        connectionToken = negotiateResponse.body().getConnectionToken();

        // ping realtime endpoint
        Response<PingResponse> pingResponse = retrofitApi.ping(SensiApi.getUnixTimestamp()).execute();
        verifyResponse(pingResponse);

        // realtime connect
        Response<ConnectResponse> connectResponse = retrofitApi.connect(
                TRANSPORT, connectionToken, CONNECTION_DATA, SensiApi.getTID(), SensiApi.getUnixTimestamp()
        ).execute();
        verifyResponse(connectResponse);
        messageId = connectResponse.body().getC();
    }

    public void subscribe() throws IOException, APIException {
        for (ThermostatResponse thermostat : thermostats) {
            // subscribe thermostat
            SubscribeRequest subRequest = new SubscribeRequest(thermostat.getiCD(), subscriptionId++);
            String subRequestBody = gson.toJson(subRequest);
            Response<SubscribeResponse> subResponse = retrofitApi
                    .subscribe(TRANSPORT, connectionToken, subRequestBody)
                    .execute();
            verifyResponse(subResponse);
        }
    }

    public void registerCallback(Consumer<OperationalStatus> callback) {
        callbacks.add(callback);
    }

    public void deregisterCallback(Consumer<OperationalStatus> callback) {
        callbacks.remove(callback);
    }

    public void deregisterAllCallbacks() {
        callbacks.clear();
    }

    public void poll() throws IOException, APIException {
        // realtime poll for messages
        Response<ResponseBody> pollResponse = retrofitApi.poll(
                TRANSPORT, connectionToken, CONNECTION_DATA, groupsToken, messageId, SensiApi.getTID(),
                SensiApi.getUnixTimestamp()
        ).execute();
        verifyResponse(pollResponse);
        String body = pollResponse.body().string();
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(body);

        // check if we have a new message, if so update this.messageId
        String newMessageId = JsonPath.parse(document).read(POLL_C_PATH, String.class);
        if (messageId.equals(newMessageId)) {
            logger.trace("old message");
        } else {
            logger.debug("new message");
            messageId = newMessageId;
            processOperationalStatus(document);
            logger.trace("OperationalStatuses: " + operationalStatuses);
        }

        // check if we have a new groups token, if so update this.groupsToken
        String newGroupsToken = JsonPath.parse(document).read(POLL_G_PATH, String.class);
        if (newGroupsToken == null || newGroupsToken.equals(groupsToken)) {
            logger.trace("old groups token");
        } else {
            logger.debug("new groups token");
            groupsToken = newGroupsToken;
        }
        logger.trace("responseBody: " + body);
    }

    // possible to have multiple status updates? (for multiple thermostats?)
    private void processOperationalStatus(Object document) throws IOException {
        try {
            String icd = JsonPath.parse(document).read(OPERATIONAL_STATUS_ICD_PATH, String.class);
            if (icd == null) {
                return;
            }
            OperationalStatus operationalStatus = JsonPath.parse(document)
                    .read(OPERATIONAL_STATUS_DATA_PATH, OperationalStatus.class);
            if (operationalStatus == null) {
                return;
            }
            OperationalStatus mergedStatus = operationalStatuses.merge(icd, operationalStatus, OperationalStatus::merge);
            callbacks.forEach(operationalStatusConsumer -> operationalStatusConsumer.accept(mergedStatus));
        } catch (PathNotFoundException e) {
            // Option.SUPPRESS_EXCEPTIONS throws AssertionError when GsonMappingProvider is used
            // ignore PathNotFoundException instead
            logger.trace("Path not found", e);
        }
    }

    public void disconnect() throws IOException, APIException {
        // realtime abort
        Response<Void> abortResponse = retrofitApi.abort(TRANSPORT, connectionToken).execute();
        verifyResponse(abortResponse);
    }

    private static void verifyResponse(Response<?> response) throws APIException {
        if (!response.isSuccessful()) {
            try {
                throw new APIException(response.errorBody().string());
            } catch (IOException e) {
                throw new APIException("Exception while reading Response.errorBody()", e);
            }
        }
    }

    private static Gson gsonFactory() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
    }

    public static RetrofitApi buildRetrofitApi() {
        return buildRetrofitApi(null, null, null);
    }

    public static RetrofitApi buildRetrofitApi(String apiUrl, CookieJar cookieJar, Collection<Interceptor> interceptors) {
        if (apiUrl == null) {
            apiUrl = "https://bus-serv.sensicomfort.com";
        }
        if (cookieJar == null) {
            cookieJar = new PersistentCookieJar();
        }
        if (interceptors == null || interceptors.isEmpty()) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            interceptors = Arrays.asList(loggingInterceptor, DEFAULT_OK_HTTP_INTERCEPTOR);
        }

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .readTimeout(60, TimeUnit.SECONDS);

        for (Interceptor interceptor : interceptors) {
            okBuilder = okBuilder.addInterceptor(interceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder()
                                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                                .create())
                )
                .client(okBuilder.build())
                .build();
        return retrofit.create(RetrofitApi.class);
    }

    private static int getTID() {
        return ThreadLocalRandom.current().nextInt(0, 11);
    }

    private static long getUnixTimestamp() {
        return System.currentTimeMillis();
    }

    public static final class Builder {
        private String username;
        private String password;
        private String apiUrl;
        private Collection<Interceptor> interceptors = new ArrayList<>();
        private CookieJar cookieJar;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        public Builder setInterceptors(Collection<Interceptor> interceptors) {
            this.interceptors = interceptors;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        public Builder setCookieJar(CookieJar cookieJar) {
            this.cookieJar = cookieJar;
            return this;
        }

        public SensiApi build() {
            return new SensiApi(username, password, SensiApi.buildRetrofitApi(apiUrl, cookieJar, interceptors));
        }
    }
}
