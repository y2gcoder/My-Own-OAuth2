package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.RefreshTokenProvider;

import java.time.Duration;

public class StubRefreshTokenProvider implements RefreshTokenProvider {
    @Override
    public String generateToken() {
        return "refreshtoken";
    }

    @Override
    public Duration getExpiration() {
        return Duration.ofMillis(10000L);
    }
}
