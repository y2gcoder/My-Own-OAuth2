package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.AuthorizationCodeProvider;
import com.y2gcoder.auth.common.application.Time;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicAuthorizationCodeProvider implements AuthorizationCodeProvider {
    private final Time time;
    private final Duration expiration;

    @Override
    public String generateCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public LocalDateTime getExpirationTime() {
        return time.now().plus(expiration);
    }
}
