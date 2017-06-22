package com.ianhattendorf.sensi.sensiapi;

import com.ianhattendorf.sensi.sensiapi.response.data.Thermostat;
import com.ianhattendorf.sensi.sensiapi.response.data.Update;
import com.ianhattendorf.sensi.sensiapi.response.data.Weather;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link RetrofitSensiApi} with mock server responses.
 */
public final class RetrofitSensiApiTest {

    private MockWebServer server;

    @Before
    public void setUp() {
        server = new MockWebServer();
    }

    @Test
    public void testHappyPath() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        HttpUrl baseUrl = server.url("/");

        SensiApi api = new RetrofitSensiApi.Builder()
                .setApiUrl(baseUrl.toString())
                .setUsername("username")
                .setPassword("password".toCharArray())
                .build();
        @SuppressWarnings("unchecked")
        BiConsumer<Thermostat, Update> callback = (BiConsumer<Thermostat, Update>) mock(BiConsumer.class);
        api.registerCallback(callback);

        server.enqueue(new MockResponse().setBody(readResponseFile("authorize.json")));
        server.enqueue(new MockResponse().setBody(readResponseFile("thermostat.json")));
        server.enqueue(new MockResponse().setBody(readResponseFile("negotiate.json")));
        server.enqueue(new MockResponse().setBody(readResponseFile("ping.json")));
        server.enqueue(new MockResponse().setBody(readResponseFile("connect.json")));
        api.start().get(30, TimeUnit.SECONDS);

        RecordedRequest request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("POST /api/authorize HTTP/1.1", request.getRequestLine());
        assertEquals("{\"Username\":\"username\",\"Password\":\"password\"}", request.getBody().readUtf8());

        request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("GET /api/thermostats HTTP/1.1", request.getRequestLine());
        assertEquals("", request.getBody().readUtf8());

        request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("GET", request.getMethod());
        assertEquals("/realtime/negotiate", request.getRequestUrl().encodedPath());
        assertTrue(TestHelper.isNumeric(request.getRequestUrl().queryParameter("_")));
        assertEquals("", request.getBody().readUtf8());


        request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("GET", request.getMethod());
        assertEquals("/realtime/ping", request.getRequestUrl().encodedPath());
        assertTrue(TestHelper.isNumeric(request.getRequestUrl().queryParameter("_")));
        assertEquals("", request.getBody().readUtf8());

        request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("GET", request.getMethod());
        assertEquals("/realtime/connect", request.getRequestUrl().encodedPath());
        assertEquals("", request.getBody().readUtf8());
        assertEquals("longPolling", request.getRequestUrl().queryParameter("transport"));
        assertEquals("connection-token-here", request.getRequestUrl().queryParameter("connectionToken"));
        assertEquals("[{\"name\":\"thermostat-v1\"}]", request.getRequestUrl().queryParameter("connectionData"));
        assertTrue(TestHelper.isNumeric(request.getRequestUrl().queryParameter("_")));
        int tid = Integer.parseInt(request.getRequestUrl().queryParameter("tid"));
        assertTrue(tid >= 0 && tid <= 10);

        server.enqueue(new MockResponse().setBody(readResponseFile("subscribe.json")));
        api.subscribe().get(30, TimeUnit.SECONDS);

        request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("POST /realtime/send?transport=longPolling&connectionToken=connection-token-here HTTP/1.1", request.getRequestLine());
        assertEquals("data=%7B%22H%22%3A%22thermostat-v1%22%2C%22M%22%3A%22Subscribe%22%2C%22A%22%3A%5B%2201-23-45-67-89-ab-cd-ef%22%5D%2C%22I%22%3A0%7D", request.getBody().readUtf8());


        Collection<Thermostat> thermostats = api.getThermostats();
        assertFalse(thermostats.isEmpty());
        Thermostat thermostat = thermostats.iterator().next();
        assertNotNull(thermostat.getiCD());

        server.enqueue(new MockResponse().setBody(readResponseFile("weather.json")));
        Weather weather = api.getWeather(thermostat.getiCD()).get(30, TimeUnit.SECONDS);
        assertNotNull(weather);
        assertNotNull(weather.getCondition());

        request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("GET /api/weather/01-23-45-67-89-ab-cd-ef HTTP/1.1", request.getRequestLine());
        assertEquals("", request.getBody().readUtf8());


        server.enqueue(new MockResponse().setBody(readResponseFile("poll1.json")));
        server.enqueue(new MockResponse().setBody(readResponseFile("poll2.json")));
        for (int i = 0; i < 2; ++i) {
            api.poll().get(30, TimeUnit.SECONDS);
        }

        request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("GET", request.getMethod());
        assertEquals("/realtime/poll", request.getRequestUrl().encodedPath());
        assertEquals("", request.getBody().readUtf8());
        assertEquals("longPolling", request.getRequestUrl().queryParameter("transport"));
        assertEquals("connection-token-here", request.getRequestUrl().queryParameter("connectionToken"));
        assertEquals("[{\"name\":\"thermostat-v1\"}]", request.getRequestUrl().queryParameter("connectionData"));
        assertEquals("0,1234567890ABC", request.getRequestUrl().queryParameter("messageId"));
        assertTrue(TestHelper.isNumeric(request.getRequestUrl().queryParameter("_")));
        tid = Integer.parseInt(request.getRequestUrl().queryParameter("tid"));
        assertTrue(tid >= 0 && tid <= 10);

        request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("GET", request.getMethod());
        assertEquals("/realtime/poll", request.getRequestUrl().encodedPath());
        assertEquals("", request.getBody().readUtf8());
        assertEquals("longPolling", request.getRequestUrl().queryParameter("transport"));
        assertEquals("connection-token-here", request.getRequestUrl().queryParameter("connectionToken"));
        assertEquals("[{\"name\":\"thermostat-v1\"}]", request.getRequestUrl().queryParameter("connectionData"));
        assertEquals("0,1234567890ABD", request.getRequestUrl().queryParameter("messageId"));
        assertTrue(TestHelper.isNumeric(request.getRequestUrl().queryParameter("_")));
        tid = Integer.parseInt(request.getRequestUrl().queryParameter("tid"));
        assertTrue(tid >= 0 && tid <= 10);


        server.enqueue(new MockResponse());
        api.disconnect().get(30, TimeUnit.SECONDS);

        request = server.takeRequest(5, TimeUnit.SECONDS);
        assertEquals("GET /realtime/abort?transport=longPolling&connectionToken=connection-token-here HTTP/1.1", request.getRequestLine());
        assertEquals("", request.getBody().readUtf8());

        verify(callback, atLeastOnce()).accept(notNull(), notNull());
        verifyNoMoreInteractions(callback);
    }

    private static Buffer readResponseFile(String fileName) throws IOException {
        return new Buffer().readFrom(TestHelper.loadFile("okhttp/responses/" + fileName));
    }
}
