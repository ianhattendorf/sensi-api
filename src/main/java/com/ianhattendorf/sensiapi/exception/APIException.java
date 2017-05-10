package com.ianhattendorf.sensiapi.exception;

public class APIException extends Exception {
    public APIException(String message) {
        super(message);
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
    }
}
