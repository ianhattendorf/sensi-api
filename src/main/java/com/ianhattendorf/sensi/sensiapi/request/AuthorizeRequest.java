package com.ianhattendorf.sensi.sensiapi.request;

public final class AuthorizeRequest {
    private final String username;
    private final String password;

    public AuthorizeRequest(String username, char[] password) {
        this.username = username;
        this.password = new String(password); // gson only reads fields...
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
