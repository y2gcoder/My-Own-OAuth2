package com.y2gcoder.auth.auth.ui;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenRefreshRequest {

    @NotBlank(message = "액세스 토큰은 필수값입니다.")
    private String accessToken;
    @NotBlank(message = "리프레시 토큰은 필수값입니다.")
    private String refreshToken;

    @Builder
    private TokenRefreshRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
