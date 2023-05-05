package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.AuthorizationCodeProvider;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicAuthorizationCodeProvider implements AuthorizationCodeProvider {
    private final Duration expiration;

    @Override
    public String generateCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public LocalDateTime getExpirationTime(LocalDateTime currentTime) {
        return currentTime.plus(expiration);
    }
}
