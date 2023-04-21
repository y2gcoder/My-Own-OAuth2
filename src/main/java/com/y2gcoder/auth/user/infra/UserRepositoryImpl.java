package com.y2gcoder.auth.user.infra;

import com.y2gcoder.auth.user.application.UserRepository;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public void save(User user) {
        UserJpaEntity userJpaEntity = UserJpaEntity.fromDomain(user);
        userJpaRepository.save(userJpaEntity);
    }

    @Override
    public UserId nextUserId() {
        return UserId.of(UUID.randomUUID().toString());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
