# sensi-api
Unofficial Sensi Java API to monitor Sensi Thermostats for changes.

## About
Sensi uses a SignalR API endpoint for their web frontend to provide realtime updates. Unfortunately, they're using an old protocol (v1.2) which isn't supported by the SignalR Java client. Fortunately, we can still communicate via the longPolling transport mode. After authenticating and subscribing to the API, we can repeatedly poll for new events. The server will either keep each request open until an update is available or send an empty response after 20 seconds.

This library doesn't handle re-authentication, you'll need to handle this yourself (`retrofit2.HttpException` with `code == 401 || code == 403`) by creating a new `RetrofitSensiApi` object.

See [ianhattendorf/sensi-monitor](https://github.com/ianhattendorf/sensi-monitor) for a usage example.

## Testing
The integration test hits the Sensi servers and expects to receive a response from an online thermostat. You'll need to provide an `app.properties` file with your credentials (see `app.properties.example`).

## Maven
```xml
<dependency>
	<groupId>com.ianhattendorf.sensi</groupId>
	<artifactId>sensi-api</artifactId>
	<version>${sensiapi.version}</version>
</dependency>
```
