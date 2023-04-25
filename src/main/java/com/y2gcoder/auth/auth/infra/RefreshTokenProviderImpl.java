package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.RefreshTokenProvider;

import java.time.Duration;
import java.util.UUID;

public class RefreshTokenProviderImpl implements RefreshTokenProvider {
    private final Duration expiration;

    public RefreshTokenProviderImpl(Duration expiration) {
        this.expiration = expiration;
    }

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Duration getExpiration() {
        return expiration;
    }
}
