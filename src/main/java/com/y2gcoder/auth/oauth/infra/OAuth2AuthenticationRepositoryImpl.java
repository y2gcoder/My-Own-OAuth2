package com.y2gcoder.auth.oauth.infra;

import com.y2gcoder.auth.oauth.application.OAuth2AuthenticationRepository;
import com.y2gcoder.auth.oauth.domain.OAuth2Authentication;
import com.y2gcoder.auth.oauth.domain.OAuth2AuthenticationId;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OAuth2AuthenticationRepositoryImpl implements OAuth2AuthenticationRepository {

    private final OAuth2AuthenticationJpaRepository oAuth2AuthenticationJpaRepository;

    @Override
    public OAuth2Authentication save(OAuth2Authentication oAuth2Authentication) {
        OAuth2AuthenticationJpaEntity entity = OAuth2AuthenticationJpaEntity.fromDomain(
                oAuth2Authentication);
        return oAuth2AuthenticationJpaRepository.save(entity).toDomain();
    }

    @Override
    public OAuth2AuthenticationId nextOAuth2AuthenticationId() {
        return OAuth2AuthenticationId.of(UUID.randomUUID().toString());
    }

    @Override
    public Optional<OAuth2Authentication> findByProviderId(String providerId) {
        return oAuth2AuthenticationJpaRepository.findByProviderId(providerId)
                .map(OAuth2AuthenticationJpaEntity::toDomain);
    }

    @Override
    public void delete(OAuth2Authentication oAuth2Authentication) {
        oAuth2AuthenticationJpaRepository.delete(
                OAuth2AuthenticationJpaEntity.fromDomain(oAuth2Authentication));
    }
}
