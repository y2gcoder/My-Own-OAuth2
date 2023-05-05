package com.y2gcoder.auth.common.ui;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private String code;
    private String message;

    @Builder
    private ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
