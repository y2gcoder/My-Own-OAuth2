package com.y2gcoder.auth.oauth.domain;

import java.util.Objects;

public class OAuth2AuthenticationId {

    private final String value;

    private OAuth2AuthenticationId(String value) {
        this.value = value;
    }

    public static OAuth2AuthenticationId of(String value) {
        return new OAuth2AuthenticationId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OAuth2AuthenticationId oAuth2AuthenticationId = (OAuth2AuthenticationId) o;
        return Objects.equals(value, oAuth2AuthenticationId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
