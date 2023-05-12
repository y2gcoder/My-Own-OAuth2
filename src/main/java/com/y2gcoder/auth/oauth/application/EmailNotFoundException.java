package com.y2gcoder.auth.oauth.application;

import org.springframework.security.core.AuthenticationException;

public class EmailNotFoundException extends AuthenticationException {

    public EmailNotFoundException() {
        super("Email attribute not found in the OAuth2 user info");
    }
}
