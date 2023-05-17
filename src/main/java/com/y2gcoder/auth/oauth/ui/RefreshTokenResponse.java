package com.y2gcoder.auth.oauth.ui;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.y2gcoder.auth.auth.application.RefreshTokenDto;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
public class RefreshTokenResponse {

    private String token;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime expirationTime;

    public RefreshTokenResponse(RefreshTokenDto dto) {
        this.token = dto.getToken();
        this.expirationTime = dto.getExpirationTime();
    }
}
