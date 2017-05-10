package com.ianhattendorf.sensiapi.response;

public final class NegotiateResponse {
    private final String url;
    private final String connectionToken;
    private final String connectionId;
    private final Integer keepAliveTimeout;
    private final Integer disconnectTimeout;
    private final Boolean tryWebSockets;
    private final String protocolVersion;

    public NegotiateResponse(String url, String connectionToken, String connectionId, Integer keepAliveTimeout,
                             Integer disconnectTimeout, Boolean tryWebSockets, String protocolVersion) {
        this.url = url;
        this.connectionToken = connectionToken;
        this.connectionId = connectionId;
        this.keepAliveTimeout = keepAliveTimeout;
        this.disconnectTimeout = disconnectTimeout;
        this.tryWebSockets = tryWebSockets;
        this.protocolVersion = protocolVersion;
    }

    public String getUrl() {
        return url;
    }

    public String getConnectionToken() {
        return connectionToken;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public Integer getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public Integer getDisconnectTimeout() {
        return disconnectTimeout;
    }

    public Boolean getTryWebSockets() {
        return tryWebSockets;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
