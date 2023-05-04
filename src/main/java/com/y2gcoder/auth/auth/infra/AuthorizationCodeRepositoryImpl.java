package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.application.AuthorizationCodeRepository;
import com.y2gcoder.auth.auth.application.NotFoundAuthorizationCodeException;
import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AuthorizationCodeRepositoryImpl implements AuthorizationCodeRepository {

    private final AuthorizationCodeJpaRepository authorizationCodeJpaRepository;

    @Override
    public AuthorizationCode save(AuthorizationCode authorizationCode) {
        AuthorizationCodeJpaEntity authorizationCodeJpaEntity = AuthorizationCodeJpaEntity
                .fromDomain(authorizationCode);
        return authorizationCodeJpaRepository.save(authorizationCodeJpaEntity).toDomain();
    }

    @Override
    public AuthorizationCodeId nextAuthorizationCodeId() {
        return AuthorizationCodeId.of(UUID.randomUUID().toString());
    }

    @Override
    public Optional<AuthorizationCode> findByCode(String code) {
        return authorizationCodeJpaRepository.findByCode(code)
                .map(AuthorizationCodeJpaEntity::toDomain);
    }

    @Transactional
    @Override
    public void update(AuthorizationCodeId id, Consumer<AuthorizationCode> modifier) {
        AuthorizationCodeJpaEntity authorizationCodeJpaEntity = authorizationCodeJpaRepository.findById(
                        id.getValue())
                .orElseThrow(NotFoundAuthorizationCodeException::new);
        AuthorizationCode authorizationCode = authorizationCodeJpaEntity.toDomain();
        modifier.accept(authorizationCode);
        authorizationCodeJpaEntity.update(authorizationCode);
    }
}
