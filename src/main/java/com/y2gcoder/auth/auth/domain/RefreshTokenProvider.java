package com.y2gcoder.auth.auth.domain;

import java.time.LocalDateTime;

public interface RefreshTokenProvider {

    String generateToken();

    LocalDateTime getExpirationTime(LocalDateTime currentTime);
}
