package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.*;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IssueAuthorizationCodeService {
    private final OwnerService ownerService;
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final AuthorizationCodeProvider authorizationCodeProvider;

    public AuthorizationCode issueAuthorizationCode(String email, String password) {
        // 1. email과 평문 password로 userId 찾기
        UserId ownerId = ownerService.getOwnerId(email, password);
        // 2. AuthorizationCode 생성
        AuthorizationCodeId authorizationCodeId = authorizationCodeRepository.nextAuthorizationCodeId();
        String code = authorizationCodeProvider.generateCode();
        LocalDateTime expirationTime = authorizationCodeProvider.getExpirationTime();
        AuthorizationCode authorizationCode = new AuthorizationCode(
                authorizationCodeId,
                code,
                expirationTime,
                AuthorizationCodeStatus.ISSUED,
                ownerId
        );
        authorizationCodeRepository.save(authorizationCode);

        return authorizationCode;
    }
}
