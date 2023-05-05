package com.y2gcoder.auth.auth.domain;

import com.y2gcoder.auth.user.domain.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuthorizationCode {

    private AuthorizationCodeId id;
    private String code;
    private LocalDateTime expirationTime;
    private AuthorizationCodeStatus status;
    private UserId ownerId;

    public AuthorizationCode(AuthorizationCodeId id,
            String code,
            LocalDateTime expirationTime,
            AuthorizationCodeStatus status,
            UserId ownerId) {
        this.id = id;
        this.code = code;
        this.expirationTime = expirationTime;
        this.status = status;
        this.ownerId = ownerId;
    }

    public AuthorizationCodeId getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public AuthorizationCodeStatus getStatus() {
        return status;
    }

    public UserId getOwnerId() {
        return ownerId;
    }

    public boolean isExpiredAt(LocalDateTime referenceTime) {
        return referenceTime.isAfter(expirationTime);
    }

    public boolean isUsed() {
        return status == AuthorizationCodeStatus.USED;
    }

    public void markAsUsed() {
        status = AuthorizationCodeStatus.USED;
    }

    public boolean isAvailable(LocalDateTime referenceTime) {
        return !isUsed() && !isExpiredAt(referenceTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthorizationCode that = (AuthorizationCode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
