package com.ianhattendorf.sensi.sensiapi.exception;

public class APIException extends RuntimeException {
    public APIException(String message) {
        super(message);
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
    }
}
