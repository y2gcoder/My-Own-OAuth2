package com.y2gcoder.auth.auth.domain;

import com.y2gcoder.auth.user.domain.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

public class RefreshToken {
    private RefreshTokenId id;
    private String token;
    private UserId ownerId;
    private LocalDateTime expirationTime;
    private LocalDateTime issuedAt;

    public RefreshToken(RefreshTokenId id,
                        String token,
                        UserId ownerId,
                        LocalDateTime expirationTime,
                        LocalDateTime issuedAt) {
        this.id = id;
        this.token = token;
        this.ownerId = ownerId;
        this.expirationTime = expirationTime;
        this.issuedAt = issuedAt;
    }

    public RefreshTokenId getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public UserId getOwnerId() {
        return ownerId;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
