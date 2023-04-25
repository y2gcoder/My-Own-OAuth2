package com.y2gcoder.auth.auth.domain;

import java.util.Objects;

public class RefreshTokenId {
    private final String value;

    private RefreshTokenId(String value) {
        this.value = value;
    }

    public static RefreshTokenId of(String value) {
        return new RefreshTokenId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshTokenId refreshTokenId = (RefreshTokenId) o;
        return Objects.equals(value, refreshTokenId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
