package com.y2gcoder.auth.auth.infra;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AuthorizationCodeJpaRepository extends Repository<AuthorizationCodeJpaEntity, String> {
    void save(AuthorizationCodeJpaEntity authorizationCodeJpaEntity);

    Optional<AuthorizationCodeJpaEntity> findById(String id);

    Optional<AuthorizationCodeJpaEntity> findByCode(String code);
}
