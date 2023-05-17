package com.y2gcoder.auth.oauth.ui;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthCodeExchangeRequest {

    @NotBlank(message = "리다이렉트 URI는 필수값입니다.")
    private String redirectUri;

    @NotBlank(message = "인증코드는 필수값입니다.")
    private String code;

    @Builder
    private AuthCodeExchangeRequest(String redirectUri, String code) {
        this.redirectUri = redirectUri;
        this.code = code;
    }
}
