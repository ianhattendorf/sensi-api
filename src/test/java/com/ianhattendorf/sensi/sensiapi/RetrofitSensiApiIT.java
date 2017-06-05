package com.ianhattendorf.sensi.sensiapi;

import com.ianhattendorf.sensi.sensiapi.response.data.Thermostat;
import com.ianhattendorf.sensi.sensiapi.response.data.Update;
import com.ianhattendorf.sensi.sensiapi.response.data.Weather;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        Properties properties = new Properties();
        Path path = Paths.get(System.getProperty("user.dir")).resolve("app.properties");
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            properties.load(inputStream);
        }

        SensiApi api = new RetrofitSensiApi.Builder()
                .setUsername(properties.getProperty("username"))
                .setPassword(properties.getProperty("password"))
                .build();
        @SuppressWarnings("unchecked")
        BiConsumer<Thermostat, Update> callback = (BiConsumer<Thermostat, Update>) mock(BiConsumer.class);
        api.registerCallback(callback);

        api.start().get(30, TimeUnit.SECONDS);

        api.subscribe().get(30, TimeUnit.SECONDS);

        Collection<Thermostat> thermostats = api.getThermostats();
        assertFalse(thermostats.isEmpty());
        Thermostat thermostat = thermostats.iterator().next();
        assertNotNull(thermostat.getiCD());

        Weather weather = api.getWeather(thermostat.getiCD()).get(30, TimeUnit.SECONDS);
        assertNotNull(weather);
        assertNotNull(weather.getCondition());

        for (int i = 0; i < 2; ++i) {
            api.poll().get(30, TimeUnit.SECONDS);
        }

        api.disconnect().get(30, TimeUnit.SECONDS);

        verify(callback, atLeastOnce()).accept(notNull(), notNull());
        verifyNoMoreInteractions(callback);
    }
}
