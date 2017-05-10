package com.ianhattendorf.sensiapi.response;

public final class PingResponse {
    private final String response;

    public PingResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
