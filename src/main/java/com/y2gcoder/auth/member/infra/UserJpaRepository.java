package com.y2gcoder.auth.member.infra;

import org.springframework.data.repository.Repository;

public interface UserJpaRepository extends Repository<UserJpaEntity, Long> {
    void save(UserJpaEntity entity);

    boolean existsByEmail(String email);
}
