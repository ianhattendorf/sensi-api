package com.ianhattendorf.sensi.sensiapi;

import com.ianhattendorf.sensi.sensiapi.response.data.Thermostat;
import com.ianhattendorf.sensi.sensiapi.response.data.Update;
import com.ianhattendorf.sensi.sensiapi.response.data.Weather;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Test {@link RetrofitSensiApi}, hitting the Sensi API endpoint.
 */
public final class RetrofitSensiApiIT {
    @Test
    public void testHappyPath() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        TestHelper.ApiCredentials apiCredentials = TestHelper.getApiCredentials();
        SensiApi api = new RetrofitSensiApi.Builder()
                .setUsername(apiCredentials.getUsername())
                .setPassword(apiCredentials.getPassword())
                .build();
        @SuppressWarnings("unchecked")
        BiConsumer<Thermostat, Update> callback = (BiConsumer<Thermostat, Update>) mock(BiConsumer.class);
        boolean[] callbackReceived = new boolean[1];
        api.registerCallback(callback);
        api.registerCallback((thermostat, update) -> callbackReceived[0] = true);

        api.start().get(30, TimeUnit.SECONDS);

        api.subscribe().get(30, TimeUnit.SECONDS);

        Collection<Thermostat> thermostats = api.getThermostats();
        assertFalse(thermostats.isEmpty());
        Thermostat thermostat = thermostats.iterator().next();
        assertNotNull(thermostat.getiCD());

        Weather weather = api.getWeather(thermostat.getiCD()).get(30, TimeUnit.SECONDS);
        assertNotNull(weather);
        assertNotNull(weather.getCondition());

        for (int i = 0; i < 5 && !callbackReceived[0]; ++i) {
            api.poll().get(30, TimeUnit.SECONDS);
        }

        api.disconnect().get(30, TimeUnit.SECONDS);

        verify(callback, atLeastOnce()).accept(notNull(), notNull());
        verifyNoMoreInteractions(callback);
    }
}
