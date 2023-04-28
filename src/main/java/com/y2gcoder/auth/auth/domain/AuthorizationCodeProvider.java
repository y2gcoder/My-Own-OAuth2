package com.y2gcoder.auth.auth.domain;

import java.time.LocalDateTime;


public interface AuthorizationCodeProvider {
    String generateCode();

    LocalDateTime getExpirationTime();
}
