package com.y2gcoder.auth.auth.ui;

import com.y2gcoder.auth.auth.application.SignInDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInResponse {
    private AccessTokenResponse access;
    private RefreshTokenResponse refresh;

    public SignInResponse(SignInDto signInDto) {
        this.access = new AccessTokenResponse(signInDto.getAccess());
        this.refresh = new RefreshTokenResponse(signInDto.getRefresh());
    }
}
