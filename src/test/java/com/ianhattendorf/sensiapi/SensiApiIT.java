package com.ianhattendorf.sensiapi;

import com.ianhattendorf.sensiapi.exception.APIException;
import com.ianhattendorf.sensiapi.response.data.OperationalStatus;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public final class SensiApiIT {
    private static final Logger log = LoggerFactory.getLogger(SensiApiIT.class);

    @Test
    public void testHappyPath() throws IOException, APIException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties"));

        SensiApi api = new SensiApi.Builder()
                .setUsername(properties.getProperty("username"))
                .setPassword(properties.getProperty("password"))
                .build();
        @SuppressWarnings("unchecked")
        Consumer<OperationalStatus> callback = (Consumer<OperationalStatus>) mock(Consumer.class);
        api.registerCallback(callback);

        api.start();
        api.subscribe();
        for (int i = 0; i < 2; ++i) {
           api.poll();
        }
        api.disconnect();

        verify(callback, atLeastOnce()).accept(notNull());
        verifyNoMoreInteractions(callback);
    }
}
