package com.y2gcoder.auth.oauth.application;

import org.springframework.security.core.AuthenticationException;

public class ProviderIdNotFoundException extends AuthenticationException {

    public ProviderIdNotFoundException() {
        super("Provider ID attribute not found in the OAuth2 user info");
    }
}
