package com.y2gcoder.auth.user.infra;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmail(String email);
}
