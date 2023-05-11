package com.y2gcoder.auth.oauth.application;

public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException() {
        super("Email attribute not found in the OAuth2 user info");
    }
}
