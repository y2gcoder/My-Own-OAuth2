package com.y2gcoder.auth.auth.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AuthorizationCodeJpaRepository extends
        JpaRepository<AuthorizationCodeJpaEntity, String> {

    Optional<AuthorizationCodeJpaEntity> findByCode(String code);
}
