package com.y2gcoder.auth.auth.application;

public class InvalidAccessTokenException extends RuntimeException {

    private static final String MESSAGE = "Authentication required. Please provide a valid token.";

    public InvalidAccessTokenException(Throwable cause) {
        super(MESSAGE, cause);
    }

    public InvalidAccessTokenException() {
        super(MESSAGE);
    }
}
