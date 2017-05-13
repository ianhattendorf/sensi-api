package com.ianhattendorf.sensi.sensiapi;

import com.ianhattendorf.sensi.sensiapi.request.AuthorizeRequest;
import com.ianhattendorf.sensi.sensiapi.response.*;
import okhttp3.ResponseBody;
import retrofit2.http.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface RetrofitApi {
    @POST("/api/authorize")
    CompletableFuture<AuthorizeResponse> authorize(@Body AuthorizeRequest authorizeRequest);

    @GET("/api/thermostats")
    CompletableFuture<List<ThermostatResponse>> thermostats();

    @GET("/realtime/negotiate")
    CompletableFuture<NegotiateResponse> negotiate(@Query("_") long ts);

    @GET("/realtime/ping")
    CompletableFuture<PingResponse> ping(@Query("_") long ts);

    @FormUrlEncoded
    @POST("/realtime/send")
    CompletableFuture<SubscribeResponse> subscribe(@Query("transport") String transport,
                                      @Query("connectionToken") String connectionToken,
                                      @Field("data") String subscribeRequest);

    @GET("/realtime/connect")
    CompletableFuture<ConnectResponse> connect(@Query("transport") String transport,
                                               @Query("connectionToken") String connectionToken,
                                               @Query(value = "connectionData", encoded = true) String connectionData,
                                               @Query("tid") int tid,
                                               @Query("_") long ts);

    @GET("/realtime/poll")
    CompletableFuture<ResponseBody> poll(@Query("transport") String transport,
                            @Query("connectionToken") String connectionToken,
                            @Query(value = "connectionData", encoded = true) String connectionData,
                            @Query("groupsToken") String groupsToken,
                            @Query("messageId") String messageId,
                            @Query("tid") int tid,
                            @Query("_") long ts);

    @GET("/realtime/abort")
    CompletableFuture<Void> abort(@Query("transport") String transport, @Query("connectionToken") String connectionToken);
}
