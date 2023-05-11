package com.y2gcoder.auth.oauth.application;

public class UnsupportedOAuth2ProviderException extends RuntimeException {

    public UnsupportedOAuth2ProviderException(String registrationId) {
        super(String.format("Unsupported OAuth2 provider with registrationId: '%s'",
                registrationId));
    }
}
