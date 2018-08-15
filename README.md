# sensi-api
[![][travis img]][travis]
[![][maven img]][maven]
[![][license img]][license]

Unofficial Sensi Java API to monitor Sensi Thermostats for changes.

**NOTE: Sensi has updated their API and started charging for web access. This library no longer works and likely requires major updates.**

## About
Sensi uses a SignalR API endpoint for their web frontend to provide realtime updates. Unfortunately, they're using an old protocol (v1.2) which isn't supported by the SignalR Java client. Fortunately, we can still communicate via the longPolling transport mode. After authenticating and subscribing to the API, we can repeatedly poll for new events. The server will either keep each request open until an update is available or send an empty response after 20 seconds.

This library doesn't handle re-authentication, you'll need to handle this yourself (`retrofit2.HttpException` with `code == 401 || code == 403`) by creating a new `RetrofitSensiApi` object.

See [ianhattendorf/sensi-monitor](https://github.com/ianhattendorf/sensi-monitor) for a usage example.

## Testing
The integration test hits the Sensi servers and expects to receive a response from an online thermostat.

You'll need to provide a properties file (defaults to `~/.sensi-api.properties`, can be overridden via `SENSI_API_PROPERTIES` environment variable) with your credentials (see `.sensi-api.properties.example`).

## Maven
```xml
<dependency>
	<groupId>com.ianhattendorf.sensi</groupId>
	<artifactId>sensi-api</artifactId>
	<version>${sensiapi.version}</version>
</dependency>
```

[travis]:https://travis-ci.org/ianhattendorf/sensi-api
[travis img]:https://travis-ci.org/ianhattendorf/sensi-api.svg?branch=master

[maven]:http://search.maven.org/#search|gav|1|g:"com.ianhattendorf.sensi"%20AND%20a:"sensi-api"
[maven img]:https://maven-badges.herokuapp.com/maven-central/com.ianhattendorf.sensi/sensi-api/badge.svg

[license]:LICENSE.txt
[license img]:https://img.shields.io/badge/License-MIT-blue.svg
