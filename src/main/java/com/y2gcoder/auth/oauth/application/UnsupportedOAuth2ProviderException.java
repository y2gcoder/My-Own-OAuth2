package com.y2gcoder.auth.oauth.application;

import org.springframework.security.core.AuthenticationException;

public class UnsupportedOAuth2ProviderException extends AuthenticationException {

    public UnsupportedOAuth2ProviderException(String registrationId) {
        super(String.format("Unsupported OAuth2 provider with registrationId: '%s'",
                registrationId));
    }
}
