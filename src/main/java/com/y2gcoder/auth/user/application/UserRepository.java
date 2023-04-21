package com.y2gcoder.auth.user.application;

import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;

public interface UserRepository {
    void save(User user);

    UserId nextUserId();

    boolean existsByEmail(String email);
}
