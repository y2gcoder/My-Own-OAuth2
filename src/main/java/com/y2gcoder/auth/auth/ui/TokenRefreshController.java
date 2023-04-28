package com.y2gcoder.auth.auth.ui;

import com.y2gcoder.auth.auth.application.TokenRefreshDto;
import com.y2gcoder.auth.auth.application.TokenRefreshService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenRefreshController {
    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<TokenRefreshResponse> tokenRefresh(@RequestBody TokenRefreshRequest request) {
        TokenRefreshDto tokenRefreshDto = tokenRefreshService
                .tokenRefresh(request.getAccessToken(), request.getRefreshToken());

        return ResponseEntity.ok(new TokenRefreshResponse(tokenRefreshDto));
    }
}
