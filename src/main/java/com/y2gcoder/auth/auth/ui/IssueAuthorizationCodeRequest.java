package com.y2gcoder.auth.auth.ui;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueAuthorizationCodeRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}
