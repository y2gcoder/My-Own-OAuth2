package com.y2gcoder.auth.user.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private UserId id;
    private String email;
    private String password;
    private String name;

    private LocalDateTime deletedAt;

    public UserId getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public User(UserId id, String email, String password, String name, LocalDateTime deletedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.deletedAt = deletedAt;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
