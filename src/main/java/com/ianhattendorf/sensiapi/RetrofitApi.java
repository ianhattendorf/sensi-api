package com.ianhattendorf.sensiapi;

import com.ianhattendorf.sensiapi.request.AuthorizeRequest;
import com.ianhattendorf.sensiapi.response.*;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface RetrofitApi {
    @POST("/api/authorize")
    Call<AuthorizeResponse> authorize(@Body AuthorizeRequest authorizeRequest);

    @GET("/api/thermostats")
    Call<List<ThermostatResponse>> thermostats();

    @GET("/realtime/negotiate")
    Call<NegotiateResponse> negotiate(@Query("_") long ts);

    @GET("/realtime/ping")
    Call<PingResponse> ping(@Query("_") long ts);

    @FormUrlEncoded
    @POST("/realtime/send")
    Call<SubscribeResponse> subscribe(@Query("transport") String transport,
                                      @Query("connectionToken") String connectionToken,
                                      @Field("data") String subscribeRequest);

    @GET("/realtime/connect")
    Call<ConnectResponse> connect(@Query("transport") String transport,
                                  @Query("connectionToken") String connectionToken,
                                  @Query(value = "connectionData", encoded = true) String connectionData,
                                  @Query("tid") int tid,
                                  @Query("_") long ts);

    @GET("/realtime/poll")
    Call<ResponseBody> poll(@Query("transport") String transport,
                            @Query("connectionToken") String connectionToken,
                            @Query(value = "connectionData", encoded = true) String connectionData,
                            @Query("groupsToken") String groupsToken,
                            @Query("messageId") String messageId,
                            @Query("tid") int tid,
                            @Query("_") long ts);

    @GET("/realtime/abort")
    Call<Void> abort(@Query("transport") String transport, @Query("connectionToken") String connectionToken);
}
