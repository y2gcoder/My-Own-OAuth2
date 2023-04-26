package com.y2gcoder.auth.auth.ui;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
public class IssueAuthorizationCodeResponse {
    private String code;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime expirationTime;
}
