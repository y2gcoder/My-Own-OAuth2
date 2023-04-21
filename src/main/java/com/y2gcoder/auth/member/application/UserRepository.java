package com.y2gcoder.auth.member.application;

import com.y2gcoder.auth.member.domain.User;
import com.y2gcoder.auth.member.domain.UserId;

public interface UserRepository {
    void save(User user);

    UserId nextUserId();

    boolean existsByEmail(String email);
}
