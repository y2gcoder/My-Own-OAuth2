package com.y2gcoder.auth.auth.ui;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequest {

    @NotBlank(message = "인증 코드는 필수값입니다.")
    private String code;

    @Builder
    private SignInRequest(String code) {
        this.code = code;
    }
}
