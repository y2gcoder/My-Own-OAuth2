package com.y2gcoder.auth.auth.infra;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends Repository<RefreshTokenJpaEntity, String> {
    void save(RefreshTokenJpaEntity entity);

    Optional<RefreshTokenJpaEntity> findById(String id);

    Optional<RefreshTokenJpaEntity> findByToken(String token);
}
