package com.y2gcoder.auth.user.application;

import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    UserId nextUserId();

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UserId id);
}
