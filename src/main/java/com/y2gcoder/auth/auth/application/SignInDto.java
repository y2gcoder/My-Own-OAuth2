package com.y2gcoder.auth.auth.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInDto {

    private AccessTokenDto access;
    private RefreshTokenDto refresh;
}
