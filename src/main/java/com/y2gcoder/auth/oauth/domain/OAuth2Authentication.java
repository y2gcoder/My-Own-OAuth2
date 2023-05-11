package com.y2gcoder.auth.oauth.domain;

import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;

public class OAuth2Authentication {

    private OAuth2AuthenticationId id;

    private UserId ownerId;
    private OAuth2Provider provider;
    private String providerId;

    private LocalDateTime createdAt;

    public OAuth2Authentication(OAuth2AuthenticationId id, UserId ownerId, OAuth2Provider provider,
            String providerId, LocalDateTime createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.provider = provider;
        this.providerId = providerId;
        this.createdAt = createdAt;
    }

    public OAuth2AuthenticationId getId() {
        return id;
    }

    public UserId getOwnerId() {
        return ownerId;
    }

    public OAuth2Provider getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
