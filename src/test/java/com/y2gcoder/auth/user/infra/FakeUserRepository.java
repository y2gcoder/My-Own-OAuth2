package com.y2gcoder.auth.user.infra;

import com.y2gcoder.auth.user.application.UserRepository;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;

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

    @Override
    public Optional<User> findByEmail(String email) {
        for (User user : fakeUsers.values()) {
            if (user.getEmail().equals(email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Map<UserId, User> getStore() {
        return Collections.unmodifiableMap(fakeUsers);
    }
}
