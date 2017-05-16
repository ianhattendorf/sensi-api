package com.ianhattendorf.sensi.sensiapi;

import com.ianhattendorf.sensi.sensiapi.response.data.Thermostat;
import com.ianhattendorf.sensi.sensiapi.response.data.Update;
import com.ianhattendorf.sensi.sensiapi.response.data.Weather;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface SensiApi {

    CompletableFuture<Void> start();

    CompletableFuture<Void> subscribe();

    CompletableFuture<Void> poll();

    CompletableFuture<Void> disconnect();

    void registerCallback(BiConsumer<Thermostat, Update> callback);

    void deregisterCallback(BiConsumer<Thermostat, Update> callback);

    void deregisterAllCallbacks();

    Collection<Thermostat> getThermostats();

    CompletableFuture<Weather> getWeather(String icd);
}
