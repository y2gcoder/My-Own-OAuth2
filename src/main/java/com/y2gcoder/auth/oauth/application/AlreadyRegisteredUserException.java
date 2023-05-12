package com.y2gcoder.auth.oauth.application;

import org.springframework.security.core.AuthenticationException;

public class AlreadyRegisteredUserException extends AuthenticationException {

    public AlreadyRegisteredUserException(String email) {
        super(String.format(
                "The email address %s is already registered. Please use a different email address or log in with existing account.",
                email));
    }
}
