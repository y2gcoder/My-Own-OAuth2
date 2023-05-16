package com.y2gcoder.auth.oauth.ui;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthCodeExchangeRequest {

    private String redirectUri;
    private String code;

    @Builder
    private AuthCodeExchangeRequest(String redirectUri, String code) {
        this.redirectUri = redirectUri;
        this.code = code;
    }
}
