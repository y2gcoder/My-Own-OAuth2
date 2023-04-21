package com.y2gcoder.auth.member.infra;

import com.y2gcoder.auth.member.application.UserRepository;
import com.y2gcoder.auth.member.domain.User;
import com.y2gcoder.auth.member.domain.UserId;

import java.util.*;

public class FakeUserRepository implements UserRepository {
    private final Map<UserId, User> fakeUsers = new HashMap<>();

    @Override
    public void save(User user) {
        fakeUsers.put(user.getId(), user);
    }

    @Override
    public UserId nextUserId() {
        return UserId.of(UUID.randomUUID().toString());
    }

    @Override
    public boolean existsByEmail(String email) {
        for (User user : fakeUsers.values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public Map<UserId, User> getStore() {
        return Collections.unmodifiableMap(fakeUsers);
    }
}
