package com.y2gcoder.auth.auth.ui;

import com.y2gcoder.auth.auth.application.SignInDto;
import com.y2gcoder.auth.auth.application.SignInService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignInController {

    private final SignInService signInService;

    @PostMapping("/auth/token")
    public ResponseEntity<SignInResponse> signIn(@RequestBody @Valid SignInRequest request) {
        LocalDateTime currentTime = LocalDateTime.now();
        SignInDto signInDto = signInService.signIn(request.getCode(), currentTime);
        return ResponseEntity.ok(new SignInResponse(signInDto));
    }
}
