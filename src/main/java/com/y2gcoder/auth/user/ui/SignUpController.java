package com.y2gcoder.auth.user.ui;

import com.y2gcoder.auth.user.application.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService signUpService;

    @PostMapping("/users")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        signUpService.signUp(request.getEmail(), request.getPassword(), request.getName(),
                request.getProfileImageUrl());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
