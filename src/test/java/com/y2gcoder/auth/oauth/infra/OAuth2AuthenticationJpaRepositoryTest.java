package com.y2gcoder.auth.oauth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.oauth.OAuth2IntegrationTestSupport;
import com.y2gcoder.auth.oauth.domain.OAuth2Provider;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OAuth2AuthenticationJpaRepositoryTest extends OAuth2IntegrationTestSupport {

    @Autowired
    private OAuth2AuthenticationJpaRepository sut;

    @AfterEach
    void tearDown() {
        sut.deleteAllInBatch();
    }

    @DisplayName("프로바이더 ID로 OAuth2 인증 정보를 조회한다.")
    @Test
    void findByProviderId() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2023, 5, 12, 9, 56);
        OAuth2AuthenticationJpaEntity entity = new OAuth2AuthenticationJpaEntity(
                "id",
                "ownerId",
                OAuth2Provider.GOOGLE,
                "providerId",
                createdAt
        );
        sut.save(entity);

        // when
        Optional<OAuth2AuthenticationJpaEntity> result = sut.findByProviderId(
                entity.getProviderId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).extracting("id", "providerId")
                .contains("id", "providerId");
    }
}