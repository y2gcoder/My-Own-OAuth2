package com.y2gcoder.auth.auth.domain;

import java.util.Objects;

public class AuthorizationCodeId {
    private final String value;

    private AuthorizationCodeId(String value) {
        this.value = value;
    }

    public static AuthorizationCodeId of(String value) {
        return new AuthorizationCodeId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationCodeId authorizationCodeId = (AuthorizationCodeId) o;
        return Objects.equals(value, authorizationCodeId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
