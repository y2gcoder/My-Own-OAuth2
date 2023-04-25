package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.application.AuthorizationCodeRepository;
import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AuthorizationCodeRepositoryImpl implements AuthorizationCodeRepository {
    private final AuthorizationCodeJpaRepository authorizationCodeJpaRepository;

    @Override
    public void save(AuthorizationCode authorizationCode) {
        AuthorizationCodeJpaEntity authorizationCodeJpaEntity = AuthorizationCodeJpaEntity
                .fromDomain(authorizationCode);
        authorizationCodeJpaRepository.save(authorizationCodeJpaEntity);
    }

    @Override
    public AuthorizationCodeId nextAuthorizationCodeId() {
        return AuthorizationCodeId.of(UUID.randomUUID().toString());
    }
}
