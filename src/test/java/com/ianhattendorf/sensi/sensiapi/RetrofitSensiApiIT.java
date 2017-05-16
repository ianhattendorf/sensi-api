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

public final class RetrofitSensiApiIT {
    @Test
    public void testHappyPath() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties"));

        SensiApi api = new RetrofitSensiApi.Builder()
                .setUsername(properties.getProperty("username"))
                .setPassword(properties.getProperty("password"))
                .build();
        @SuppressWarnings("unchecked")
        BiConsumer<Thermostat, Update> callback = (BiConsumer<Thermostat, Update>) mock(BiConsumer.class);
        api.registerCallback(callback);

        api.start().thenRun(api::subscribe).get(30, TimeUnit.SECONDS);
        Collection<Thermostat> thermostats = api.getThermostats();
        assertFalse(thermostats.isEmpty());
        Weather weather = api.getWeather(thermostats.iterator().next().getiCD()).get(30, TimeUnit.SECONDS);
        assertNotNull(weather);
        for (int i = 0; i < 2; ++i) {
           api.poll().get(30, TimeUnit.SECONDS);
        }
        api.disconnect().get(30, TimeUnit.SECONDS);

        verify(callback, atLeastOnce()).accept(notNull(), notNull());
        verifyNoMoreInteractions(callback);
    }
}
