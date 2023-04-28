package com.y2gcoder.auth.auth.application;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AccessTokenDto {
    private String token;
    private LocalDateTime expirationTime;
}
