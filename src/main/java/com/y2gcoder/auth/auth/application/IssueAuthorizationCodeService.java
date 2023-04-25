package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.*;
import com.y2gcoder.auth.common.application.Time;
import com.y2gcoder.auth.user.domain.UserId;

import java.time.LocalDateTime;

public class IssueAuthorizationCodeService {
    private final OwnerService ownerService;
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final Time time;
    private final Long expirationMinutes;

    public IssueAuthorizationCodeService(OwnerService ownerService,
                                         AuthorizationCodeRepository authorizationCodeRepository,
                                         Time time,
                                         Long expirationMinutes) {
        this.ownerService = ownerService;
        this.authorizationCodeRepository = authorizationCodeRepository;
        this.time = time;
        this.expirationMinutes = expirationMinutes != null ? expirationMinutes : 5L;
    }

    public AuthorizationCode issueAuthorizationCode(String email, String password) {
        // 1. email과 평문 password로 userId 찾기
        UserId ownerId = ownerService.getOwnerId(email, password);
        // 2. AuthorizationCode 생성
        AuthorizationCodeId authorizationCodeId = authorizationCodeRepository.nextAuthorizationCodeId();
        String code = CodeGenerator.generateCode();
        LocalDateTime expirationTime = time.now().plusMinutes(expirationMinutes);
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
