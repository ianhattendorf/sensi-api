package com.ianhattendorf.sensiapi.response;

public final class AuthorizeResponse {
    private final String token;
    private final String expires;
    private final Boolean passwordResetRequired;
    private final Boolean alertOptIn;
    private final Boolean offersOptIn;
    private final Boolean showEula;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;

    public AuthorizeResponse(String token, String expires, Boolean passwordResetRequired, Boolean alertOptIn,
                             Boolean offersOptIn, Boolean showEula, String firstName, String lastName,
                             String phoneNumber) {
        this.token = token;
        this.expires = expires;
        this.passwordResetRequired = passwordResetRequired;
        this.alertOptIn = alertOptIn;
        this.offersOptIn = offersOptIn;
        this.showEula = showEula;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public String getExpires() {
        return expires;
    }

    public Boolean getPasswordResetRequired() {
        return passwordResetRequired;
    }

    public Boolean getAlertOptIn() {
        return alertOptIn;
    }

    public Boolean getOffersOptIn() {
        return offersOptIn;
    }

    public Boolean getShowEula() {
        return showEula;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
