package com.ianhattendorf.sensiapi;

import com.ianhattendorf.sensiapi.response.data.OperationalStatus;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.*;

public final class SensiApiIT {
    @Test
    public void testHappyPath() throws IOException, ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties"));

        SensiApi api = new SensiApi.Builder()
                .setUsername(properties.getProperty("username"))
                .setPassword(properties.getProperty("password"))
                .build();
        @SuppressWarnings("unchecked")
        BiConsumer<String, OperationalStatus> callback = (BiConsumer<String, OperationalStatus>) mock(BiConsumer.class);
        api.registerCallback(callback);

        api.start().thenRun(api::subscribe).get();
        for (int i = 0; i < 2; ++i) {
           api.poll().get();
        }
        api.disconnect().get();

        verify(callback, atLeastOnce()).accept(notNull(), notNull());
        verifyNoMoreInteractions(callback);
    }
}
