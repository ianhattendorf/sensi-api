package com.ianhattendorf.sensi.sensiapi;

import com.ianhattendorf.sensi.sensiapi.response.data.Update;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.*;

public final class RetrofitSensiApiIT {
    @Test
    public void testHappyPath() throws IOException, ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties"));

        SensiApi api = new RetrofitSensiApi.Builder()
                .setUsername(properties.getProperty("username"))
                .setPassword(properties.getProperty("password"))
                .build();
        @SuppressWarnings("unchecked")
        BiConsumer<String, Update> callback = (BiConsumer<String, Update>) mock(BiConsumer.class);
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
