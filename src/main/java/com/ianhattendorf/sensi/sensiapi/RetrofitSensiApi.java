package com.ianhattendorf.sensi.sensiapi;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ianhattendorf.sensi.sensiapi.request.AuthorizeRequest;
import com.ianhattendorf.sensi.sensiapi.response.data.Thermostat;
import com.ianhattendorf.sensi.sensiapi.exception.APIException;
import com.ianhattendorf.sensi.sensiapi.request.SubscribeRequest;
import com.ianhattendorf.sensi.sensiapi.response.data.Update;
import com.ianhattendorf.sensi.sensiapi.response.data.Weather;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.java8.Java8CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RetrofitSensiApi implements SensiApi {
    private final RetrofitApi retrofitApi;
    private final String username;
    private final char[] password;
    private int subscriptionId = 0;
    private String connectionToken;
    private String groupsToken;
    private String messageId;
    private Map<String, Thermostat> thermostatMap;
    private Map<String, Update> thermostatUpdateMap = new HashMap<>();
    private final Set<BiConsumer<Thermostat, Update>> callbacks = new LinkedHashSet<>();
    private final Gson gson = gsonFactory();

    private static final Logger logger = LoggerFactory.getLogger(RetrofitSensiApi.class);
    private static final String TRANSPORT = "longPolling";
    private static final String DEFAULT_SENSI_API_URL = "https://bus-serv.sensicomfort.com";
    // [{"name":"thermostat-v1"}]
    private static final String CONNECTION_DATA = "%5B%7B%22name%22%3A%22thermostat-v1%22%7D%5D";
    private static final String UPDATE_ICD_PATH = "$.M[0].A[0]";
    private static final String UPDATE_DATA_PATH = "$.M[0].A[1]";
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

    /**
     * Construct the {@link RetrofitSensiApi}.
     * @param username Sensi API username.
     * @param password Sensi API password.
     * @param retrofitApi {@link RetrofitApi} instance.
     * @see RetrofitSensiApi.Builder
     */
    public RetrofitSensiApi(String username, char[] password, RetrofitApi retrofitApi) {
        this.username = username;
        this.password = password;
        this.retrofitApi = retrofitApi;
    }

    public CompletableFuture<Void> start() {
        // authorize (get auth cookie)
        return retrofitApi.authorize(new AuthorizeRequest(username, password))
                // get thermostats
                .thenCompose(authorizeResponse -> {
                    Arrays.fill(password, '\0');
                    logger.debug("authorized successfully");
                    return retrofitApi.thermostats();
                })
                // realtime negotiate (get connectionToken)
                .thenCompose(thermostatResponses -> {
                    thermostatMap = thermostatResponses.stream()
                            .collect(Collectors.toMap(Thermostat::getiCD, Function.identity()));
                    logger.debug("fetched {} thermostat(s) successfully", thermostatMap.size());
                    if (logger.isTraceEnabled()) {
                        logger.trace("thermostats: {}", Arrays.toString(thermostatMap.values().toArray()));
                    }
                    return retrofitApi.negotiate(RetrofitSensiApi.getUnixTimestamp());
                })
                // ping realtime endpoint
                .thenCompose(negotiateResponse -> {
                    logger.debug("negotiated successfully");
                    connectionToken = negotiateResponse.getConnectionToken();
                    return retrofitApi.ping(RetrofitSensiApi.getUnixTimestamp());
                    // realtime connect
                }).thenCompose(pingResponse -> {
                    logger.debug("pinged successfully");
                    return retrofitApi.connect(
                            TRANSPORT, connectionToken, CONNECTION_DATA, RetrofitSensiApi.getTID(), RetrofitSensiApi.getUnixTimestamp()
                    );
                }).thenAccept(connectResponse -> {
                    logger.info("connected successfully");
                    messageId = connectResponse.getC();
                });
    }

    public CompletableFuture<Void> subscribe() {
        List<CompletableFuture<Void>> futures = thermostatMap.entrySet().stream().map(thermostat -> {
            SubscribeRequest subRequest = new SubscribeRequest(thermostat.getKey(), subscriptionId++);
            String subRequestBody = gson.toJson(subRequest);
            return retrofitApi.subscribe(TRANSPORT, connectionToken, subRequestBody)
                    .thenAccept(subscribeResponse -> {
                        logger.debug("successfully subscribed I: {}", subscribeResponse.getI());
                    });
        }).collect(Collectors.toList());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
    }

    public CompletableFuture<Void> poll() {
        // realtime poll for messages
        return retrofitApi.poll(
                TRANSPORT, connectionToken, CONNECTION_DATA, groupsToken, messageId, RetrofitSensiApi.getTID(),
                RetrofitSensiApi.getUnixTimestamp()
        ).thenAccept(responseBody -> {
            logger.info("received poll response");
            String body;
            try {
                body = responseBody.string();
            } catch (IOException e) {
                throw new APIException("Exception while reading poll response body", e);
            }
            logger.trace("responseBody: {}", body);
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(body);

            // check if we have a new message, if so update this.messageId
            String newMessageId = JsonPath.parse(document).read(POLL_C_PATH, String.class);
            if (messageId.equals(newMessageId)) {
                logger.trace("old message");
            } else {
                logger.debug("new message");
                messageId = newMessageId;
                try {
                    processUpdate(document);
                } catch (IOException e) {
                    throw new APIException("Exception while processing poll response body", e);
                }
                logger.trace("Updates: {}", thermostatUpdateMap);
            }

            // check if we have a new groups token, if so update this.groupsToken
            String newGroupsToken = JsonPath.parse(document).read(POLL_G_PATH, String.class);
            if (newGroupsToken == null || newGroupsToken.equals(groupsToken)) {
                logger.trace("old groups token");
            } else {
                logger.debug("new groups token");
                groupsToken = newGroupsToken;
            }
        });
    }

    public CompletableFuture<Void> disconnect() {
        // realtime abort
        return retrofitApi.abort(TRANSPORT, connectionToken)
                .thenRun(() -> {
                    logger.info("disconnected successfully");
                });
    }

    public void registerCallback(BiConsumer<Thermostat, Update> callback) {
        callbacks.add(callback);
    }

    public void deregisterCallback(BiConsumer<Thermostat, Update> callback) {
        callbacks.remove(callback);
    }

    public void deregisterAllCallbacks() {
        callbacks.clear();
    }

    public Collection<Thermostat> getThermostats() {
        return thermostatMap.values();
    }

    public CompletableFuture<Weather> getWeather(String icd) {
        return retrofitApi.weather(icd).thenApply(weather -> {
            logger.debug("Received weather: {}", weather);
            return weather;
        });
    }

    // possible to have multiple status updates? (for multiple thermostats?)
    private void processUpdate(Object document) throws IOException {
        try {
            String icd = JsonPath.parse(document).read(UPDATE_ICD_PATH, String.class);
            if (icd == null) {
                return;
            }
            Update update = JsonPath.parse(document)
                    .read(UPDATE_DATA_PATH, Update.class);
            if (update == null) {
                return;
            }
            Update mergedUpdate = thermostatUpdateMap.merge(icd, update, Update::merge);
            logger.debug("notifying {} callback(s) of updated status", callbacks.size());
            logger.trace("updated status [{}]: {}", icd, mergedUpdate);
            callbacks.forEach(updateConsumer -> updateConsumer.accept(thermostatMap.get(icd), mergedUpdate));
        } catch (PathNotFoundException e) {
            // Option.SUPPRESS_EXCEPTIONS throws AssertionError when GsonMappingProvider is used
            // ignore PathNotFoundException instead
            logger.trace("Path not found: {}", e.getMessage());
        }
    }

    private static Gson gsonFactory() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
    }

    /**
     * Creates a default {@link RetrofitApi} instance.
     * @return The {@link RetrofitApi} instance.
     */
    public static RetrofitApi buildRetrofitApi() {
        return buildRetrofitApi(null, null, null);
    }

    /**
     * Contstruct a {@link RetrofitApi} instance. See {@link RetrofitSensiApi.Builder} for additional information.
     * @param apiUrl Sensi API endpoint url.
     * @param cookieJar {@link CookieJar} implementation.
     * @param interceptors okhttp interceptors.
     * @return {@link RetrofitApi} instance.
     * @see RetrofitSensiApi.Builder
     */
    public static RetrofitApi buildRetrofitApi(String apiUrl, CookieJar cookieJar, Collection<Interceptor> interceptors) {
        if (apiUrl == null) {
            apiUrl = DEFAULT_SENSI_API_URL;
        }
        if (cookieJar == null) {
            cookieJar = new PersistentCookieJar();
        }
        if (interceptors == null || interceptors.isEmpty()) {
            interceptors = Collections.singletonList(DEFAULT_OK_HTTP_INTERCEPTOR);
        }

        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .readTimeout(60, TimeUnit.SECONDS);

        for (Interceptor interceptor : interceptors) {
            okBuilder = okBuilder.addInterceptor(interceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addCallAdapterFactory(Java8CallAdapterFactory.create())
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

    /**
     * Builder for {@link RetrofitSensiApi}
     */
    public static final class Builder {
        private String username;
        private char[] password;
        private String apiUrl;
        private Collection<Interceptor> interceptors = new ArrayList<>();
        private CookieJar cookieJar;

        /**
         * Set Sensi API username
         * @param username username
         * @return The builder for chaining
         */
        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        /**
         * Set Sensi API password
         * @param password password
         * @return The builder for chaining
         */
        public Builder setPassword(char[] password) {
            this.password = password;
            return this;
        }

        /**
         * Set Sensi API URL endpoint, defaults to {@link RetrofitSensiApi#DEFAULT_SENSI_API_URL}
         * @param apiUrl apiUrl
         * @return The builder for chaining
         */
        public Builder setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        /**
         * Set the okhttp interceptors to add
         * NOTE: If any interceptors are added, the default interceptor used to set headers will not be added. You will
         * need to either implement your own or add the {@link RetrofitSensiApi#DEFAULT_OK_HTTP_INTERCEPTOR} yourself.
         * @param interceptors interceptors
         * @return The builder for chaining
         */
        public Builder setInterceptors(Collection<Interceptor> interceptors) {
            this.interceptors = interceptors;
            return this;
        }

        /**
         * Add an okhttp interceptor
         * NOTE: If any interceptors are added, the default interceptor used to set headers will not be added. You will
         * need to either implement your own or add the {@link RetrofitSensiApi#DEFAULT_OK_HTTP_INTERCEPTOR} yourself.
         * @param interceptor interceptor
         * @return The builder for chaining
         */
        public Builder addInterceptor(Interceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        /**
         * Set the {@link CookieJar} implementation to use.
         * @param cookieJar cookieJar.
         * @return The builder for chaining.
         */
        public Builder setCookieJar(CookieJar cookieJar) {
            this.cookieJar = cookieJar;
            return this;
        }

        /**
         * Create the {@link SensiApi} instance.
         * @return The {@link SensiApi} instance.
         */
        public SensiApi build() {
            SensiApi sensiApi = new RetrofitSensiApi(username, password, RetrofitSensiApi.buildRetrofitApi(apiUrl, cookieJar, interceptors));
            password = null;
            return sensiApi;
        }
    }
}
