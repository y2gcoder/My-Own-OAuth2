package com.y2gcoder.auth.auth.domain;

public class NotFoundOwnerException extends RuntimeException {
    public NotFoundOwnerException(String message) {
        super(message);
    }
}
