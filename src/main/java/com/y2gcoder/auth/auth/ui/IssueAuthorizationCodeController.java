package com.y2gcoder.auth.auth.ui;

import com.y2gcoder.auth.auth.application.IssueAuthorizationCodeService;
import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IssueAuthorizationCodeController {
    private final IssueAuthorizationCodeService issueAuthorizationCodeService;

    @PostMapping("/auth/code")
    public ResponseEntity<IssueAuthorizationCodeResponse> issueAuthorizationCode(
            @RequestBody @Valid IssueAuthorizationCodeRequest request) {
        AuthorizationCode authorizationCode = issueAuthorizationCodeService
                .issueAuthorizationCode(request.getEmail(), request.getPassword());

        IssueAuthorizationCodeResponse body = new IssueAuthorizationCodeResponse(authorizationCode.getCode(),
                authorizationCode.getExpirationTime());
        return ResponseEntity.ok(body);
    }
}
