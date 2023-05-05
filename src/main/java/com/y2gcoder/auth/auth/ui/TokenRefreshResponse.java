package com.y2gcoder.auth.auth.ui;

import com.y2gcoder.auth.auth.application.TokenRefreshDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRefreshResponse {

    private AccessTokenResponse access;
    private RefreshTokenResponse refresh;

    public TokenRefreshResponse(TokenRefreshDto dto) {
        this.access = new AccessTokenResponse(dto.getAccess());
        this.refresh = new RefreshTokenResponse(dto.getRefresh());
    }
}
