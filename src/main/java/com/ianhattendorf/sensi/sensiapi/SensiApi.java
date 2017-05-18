package com.ianhattendorf.sensi.sensiapi;

import com.ianhattendorf.sensi.sensiapi.response.data.Thermostat;
import com.ianhattendorf.sensi.sensiapi.response.data.Update;
import com.ianhattendorf.sensi.sensiapi.response.data.Weather;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * An interface for accessing Sensi's API.
 */
public interface SensiApi {

    /**
     * Initialize the API.
     * @return CompletableFuture indicating the API has been initialized.
     */
    CompletableFuture<Void> start();

    /**
     * Subscribe to thermostat updates.
     * @return CompletableFuture indicating updates have been subscribed to.
     */
    CompletableFuture<Void> subscribe();

    /**
     * Poll for updates. The API utilizes long polling, and will leave a connection open for approximately 20 seconds
     * before closing it.
     * Updates will be sent out through callbacks, see {@link #registerCallback(BiConsumer) registerCallback(BiConsumer&lt;Thermostat, Update&gt;)}
     * @return CompletableFuture indicating the server has sent a response.
     */
    CompletableFuture<Void> poll();

    /**
     * Disconnect from thermostat subscription events.
     * @return CompletableFuture indicating the disconnect has completed.
     */
    CompletableFuture<Void> disconnect();

    /**
     * Register a callback to receive thermostat updates.
     * @param callback The callback that will be called when an update is received.
     */
    void registerCallback(BiConsumer<Thermostat, Update> callback);

    /**
     * Deregister a callback.
     * @param callback The callback to deregister.
     */
    void deregisterCallback(BiConsumer<Thermostat, Update> callback);

    /**
     * Deregister all callbacks.
     */
    void deregisterAllCallbacks();

    /**
     * Get all thermostats for this account.
     * @return This account's thermostats.
     */
    Collection<Thermostat> getThermostats();

    /**
     * Retrieve the current weather at the thermostat's location.
     * @param icd The thermostat's identifier.
     * @return CompletableFuture with the thermostat's current weather.
     */
    CompletableFuture<Weather> getWeather(String icd);
}
