package com.y2gcoder.auth.oauth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.oauth.OAuth2IntegrationTestSupport;
import com.y2gcoder.auth.oauth.application.OAuth2AuthenticationRepository;
import com.y2gcoder.auth.oauth.domain.OAuth2Authentication;
import com.y2gcoder.auth.oauth.domain.OAuth2AuthenticationId;
import com.y2gcoder.auth.oauth.domain.OAuth2Provider;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OAuth2AuthenticationRepositoryTest extends OAuth2IntegrationTestSupport {

    @Autowired
    private OAuth2AuthenticationJpaRepository oAuth2AuthenticationJpaRepository;

    @Autowired
    private OAuth2AuthenticationRepository sut;

    @AfterEach
    void tearDown() {
        oAuth2AuthenticationJpaRepository.deleteAllInBatch();
    }

    @DisplayName("매번 새로운 OAuth2 인증정보 ID를 생성한다.")
    @Test
    void nextOAuth2AuthenticationId() {
        // given
        Set<OAuth2AuthenticationId> oAuth2AuthenticationIds = new HashSet<>();

        // when
        for (int i = 0; i < 5; i++) {
            oAuth2AuthenticationIds.add(sut.nextOAuth2AuthenticationId());
        }

        // then
        assertThat(oAuth2AuthenticationIds).hasSize(5);

    }

    @DisplayName("OAuth2 인증 정보를 저장한다.")
    @Test
    void save() {
        // given
        OAuth2AuthenticationId id = OAuth2AuthenticationId.of("OAuth2");
        UserId ownerId = UserId.of("userId");
        LocalDateTime createdAt = LocalDateTime.of(2023, 5, 12, 9, 56);
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(
                id,
                ownerId,
                OAuth2Provider.GOOGLE,
                "providerid",
                createdAt
        );

        // when
        sut.save(oAuth2Authentication);

        // then
        Optional<OAuth2AuthenticationJpaEntity> entityOptional = oAuth2AuthenticationJpaRepository.findById(
                id.getValue());
        assertThat(entityOptional).isPresent();
        assertThat(entityOptional.get())
                .extracting("id", "ownerId", "provider", "providerId", "createdAt")
                .contains(
                        id.getValue(),
                        ownerId.getValue(),
                        OAuth2Provider.GOOGLE,
                        "providerid",
                        createdAt
                );
    }

    @DisplayName("프로바이더 ID로 OAuth2 인증 정보를 조회한다.")
    @Test
    void findByProviderId() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2023, 5, 12, 9, 56);
        OAuth2AuthenticationJpaEntity jpaEntity = new OAuth2AuthenticationJpaEntity(
                "id",
                "ownerId",
                OAuth2Provider.GOOGLE,
                "providerId",
                createdAt
        );
        oAuth2AuthenticationJpaRepository.save(jpaEntity);

        // when
        Optional<OAuth2Authentication> result = sut.findByProviderId(
                jpaEntity.getProviderId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get())
                .extracting("id", "ownerId", "provider", "providerId", "createdAt")
                .contains(OAuth2AuthenticationId.of("id"), UserId.of("ownerId"),
                        OAuth2Provider.GOOGLE, "providerId", createdAt);
    }

    @DisplayName("OAuth2 인증 정보를 삭제할 수 있다.")
    @Test
    void delete() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2023, 5, 12, 9, 56);
        OAuth2AuthenticationJpaEntity jpaEntity = oAuth2AuthenticationJpaRepository.save(
                new OAuth2AuthenticationJpaEntity(
                        "id",
                        "ownerId",
                        OAuth2Provider.GOOGLE,
                        "providerId",
                        createdAt
                ));

        OAuth2Authentication oAuth2Authentication = jpaEntity.toDomain();

        // when
        sut.delete(oAuth2Authentication);

        // then
        assertThat(oAuth2AuthenticationJpaRepository.findAll()).isEmpty();
        assertThat(
                oAuth2AuthenticationJpaRepository.findById(oAuth2Authentication.getId().getValue()))
                .isEmpty();

    }
}