package com.ianhattendorf.sensiapi.request;

public final class ConnectionData {
    private final String name;

    public ConnectionData() {
        this.name = "thermostat-v1";
    }

    public String getName() {
        return name;
    }
}
