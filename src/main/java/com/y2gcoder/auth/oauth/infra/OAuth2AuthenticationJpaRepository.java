package com.y2gcoder.auth.oauth.infra;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2AuthenticationJpaRepository extends
        JpaRepository<OAuth2AuthenticationJpaEntity, String> {

    Optional<OAuth2AuthenticationJpaEntity> findByProviderId(String providerId);
}
