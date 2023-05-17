package com.y2gcoder.auth.oauth.ui;

import com.y2gcoder.auth.auth.application.AccessTokenDto;
import com.y2gcoder.auth.auth.application.RefreshTokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuth2SignInResponse {

    private AccessTokenResponse access;
    private RefreshTokenResponse refresh;

    public OAuth2SignInResponse(AccessTokenDto accessTokenDto, RefreshTokenDto refreshTokenDto) {
        this.access = new AccessTokenResponse(accessTokenDto);
        this.refresh = new RefreshTokenResponse(refreshTokenDto);
    }
}
