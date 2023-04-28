package com.y2gcoder.auth.auth.domain;

import java.time.Duration;

public interface RefreshTokenProvider {
    String generateToken();

    Duration getExpiration();
}
