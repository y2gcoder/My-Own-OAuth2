package com.y2gcoder.auth.auth.infra;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationCodeJpaRepository extends
        JpaRepository<AuthorizationCodeJpaEntity, String> {

    Optional<AuthorizationCodeJpaEntity> findByCode(String code);
}
