package com.y2gcoder.auth.oauth.infra;

import com.y2gcoder.auth.oauth.domain.OAuth2Authentication;
import com.y2gcoder.auth.oauth.domain.OAuth2AuthenticationId;
import com.y2gcoder.auth.oauth.domain.OAuth2Provider;
import com.y2gcoder.auth.user.domain.UserId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "oauth2_authentications")
public class OAuth2AuthenticationJpaEntity {

    @Id
    private String id;

    private String ownerId;

    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    private String providerId;

    private LocalDateTime createdAt;

    public static OAuth2AuthenticationJpaEntity fromDomain(
            OAuth2Authentication oAuth2Authentication) {
        return new OAuth2AuthenticationJpaEntity(
                oAuth2Authentication.getId().getValue(),
                oAuth2Authentication.getOwnerId().getValue(),
                oAuth2Authentication.getProvider(),
                oAuth2Authentication.getProviderId(),
                oAuth2Authentication.getCreatedAt()
        );
    }

    public OAuth2Authentication toDomain() {
        return new OAuth2Authentication(
                OAuth2AuthenticationId.of(id),
                UserId.of(ownerId),
                provider,
                providerId,
                createdAt
        );
    }
}
