package com.y2gcoder.auth.user.infra;

import com.y2gcoder.auth.user.application.UserRepository;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Transactional
    @Override
    public User save(User user) {
        UserJpaEntity userJpaEntity = UserJpaEntity.fromDomain(user);
        return userJpaRepository.save(userJpaEntity).toDomain();
    }

    @Override
    public UserId nextUserId() {
        return UserId.of(UUID.randomUUID().toString());
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserJpaEntity> optionalUserJpaEntity = userJpaRepository.findByEmail(email);
        return optionalUserJpaEntity.map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.getValue())
                .map(UserJpaEntity::toDomain);
    }
}
