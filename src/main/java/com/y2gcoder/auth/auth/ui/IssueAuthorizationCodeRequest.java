package com.y2gcoder.auth.auth.ui;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IssueAuthorizationCodeRequest {

    @NotBlank(message = "이메일은 필수값입니다.")
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수값입니다.")
    private String password;

    @Builder
    private IssueAuthorizationCodeRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
