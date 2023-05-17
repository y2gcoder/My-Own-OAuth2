package com.y2gcoder.auth.oauth.application;

import org.springframework.security.core.AuthenticationException;

public class DeletedUserException extends AuthenticationException {

    public DeletedUserException() {
        super("The user has been deleted.");
    }
}
