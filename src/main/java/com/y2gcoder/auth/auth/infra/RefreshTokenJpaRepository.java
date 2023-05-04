package com.y2gcoder.auth.auth.infra;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, String> {

    Optional<RefreshTokenJpaEntity> findFirstByOwnerIdOrderByIssuedAtDesc(String ownerId);
}
