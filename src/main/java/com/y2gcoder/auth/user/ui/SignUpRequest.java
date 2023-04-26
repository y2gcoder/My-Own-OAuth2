package com.y2gcoder.auth.user.ui;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    @NotBlank
    @Email
    private String email;
    @Size(min = 8)
    @NotBlank
    private String password;
    @Size(min = 2)
    @NotBlank
    private String name;
}
