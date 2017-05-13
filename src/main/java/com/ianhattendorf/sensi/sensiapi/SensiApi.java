package com.ianhattendorf.sensi.sensiapi;

import com.ianhattendorf.sensi.sensiapi.response.data.OperationalStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface SensiApi {

    CompletableFuture<Void> start();

    CompletableFuture<Void> subscribe();

    CompletableFuture<Void> poll();

    CompletableFuture<Void> disconnect();

    void registerCallback(BiConsumer<String, OperationalStatus> callback);

    void deregisterCallback(BiConsumer<String, OperationalStatus> callback);

    void deregisterAllCallbacks();
}
