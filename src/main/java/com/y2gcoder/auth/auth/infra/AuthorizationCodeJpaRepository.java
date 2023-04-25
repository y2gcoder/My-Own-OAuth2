package com.y2gcoder.auth.auth.infra;

import org.springframework.data.repository.Repository;

public interface AuthorizationCodeJpaRepository extends Repository<AuthorizationCodeJpaEntity, Long> {
    void save(AuthorizationCodeJpaEntity authorizationCodeJpaEntity);
}
