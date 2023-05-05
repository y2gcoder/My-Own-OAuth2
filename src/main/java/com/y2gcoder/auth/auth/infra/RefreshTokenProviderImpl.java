package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.RefreshTokenProvider;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshTokenProviderImpl implements RefreshTokenProvider {

    private final Duration expiration;

    public RefreshTokenProviderImpl(Duration expiration) {
        this.expiration = expiration;
    }

    @Override
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime getExpirationTime(LocalDateTime currentTime) {
        return currentTime.plus(expiration);
    }
}
