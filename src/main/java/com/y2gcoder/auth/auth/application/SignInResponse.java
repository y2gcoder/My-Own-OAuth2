package com.y2gcoder.auth.auth.application;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInResponse {
    private AccessTokenDto access;
    private RefreshTokenDto refresh;
}
