package com.ianhattendorf.sensi.sensiapi.request;

public final class AuthorizeRequest {
    private final String username;
    private final String password;

    public AuthorizeRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
