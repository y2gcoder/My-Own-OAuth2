package com.y2gcoder.auth.oauth.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {

    GOOGLE("google"), KAKAO("kakao"), UNSUPPORTED("unsupported");

    private final String registrationId;

    public static OAuth2Provider getByRegistrationId(String registrationId) {
        for (OAuth2Provider provider : values()) {
            if (provider.getRegistrationId().equals(registrationId)) {
                return provider;
            }
        }
        return UNSUPPORTED;
    }
}
