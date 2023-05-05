package com.y2gcoder.auth.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.auth.AuthIntegrationTestSupport;
import com.y2gcoder.auth.auth.application.AuthorizationCodeRepository;
import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeStatus;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class AuthorizationCodeRepositoryTest extends AuthIntegrationTestSupport {

    @Autowired
    private AuthorizationCodeJpaRepository authorizationCodeJpaRepository;

    @Autowired
    private AuthorizationCodeRepository sut;

    @AfterEach
    void tearDown() {
        authorizationCodeJpaRepository.deleteAllInBatch();
    }

    @DisplayName("매번 새로운 인증코드 Id를 생성한다.")
    @Test
    void nextAuthorizationCodeId() {
        // given
        Set<AuthorizationCodeId> authorizationCodeIds = new HashSet<>();

        // when
        for (int i = 0; i < 5; i++) {
            authorizationCodeIds.add(sut.nextAuthorizationCodeId());
        }

        // then
        assertThat(authorizationCodeIds).hasSize(5);
    }

    @DisplayName("인증코드를 저장한다.")
    @Test
    void save() {
        // given
        AuthorizationCodeId authorizationCodeId = AuthorizationCodeId.of("authorizationCodeId");
        UserId ownerId = UserId.of("userId");
        AuthorizationCode authorizationCode = new AuthorizationCode(
                authorizationCodeId,
                "code",
                LocalDateTime.of(2023, 5, 4, 13, 27),
                AuthorizationCodeStatus.ISSUED,
                ownerId
        );

        // when
        AuthorizationCode result = sut.save(authorizationCode);

        // then
        Optional<AuthorizationCodeJpaEntity> entityOptional = authorizationCodeJpaRepository
                .findById(result.getId().getValue());
        assertThat(entityOptional).isPresent();
    }

    @DisplayName("code로 인증 코드 도메인을 조회할 수 있다.")
    @Test
    void findByCode() {
        // given
        String targetCode = "code";
        LocalDateTime expirationTime = LocalDateTime.of(2023, 5, 4, 14, 25);
        authorizationCodeJpaRepository.save(
                new AuthorizationCodeJpaEntity(
                        "id",
                        targetCode,
                        AuthorizationCodeStatus.ISSUED,
                        expirationTime,
                        "userId"
                ));

        // when
        Optional<AuthorizationCode> result = sut.findByCode(targetCode);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(AuthorizationCodeId.of("id"));

    }

    @DisplayName("컨슈머를 받아 인증 코드를 수정할 수 있다.")
    @Test
    void update() {
        // given
        String targetCode = "code";
        LocalDateTime expirationTime = LocalDateTime.of(2023, 5, 4, 14, 25);
        authorizationCodeJpaRepository.save(
                new AuthorizationCodeJpaEntity(
                        "id",
                        targetCode,
                        AuthorizationCodeStatus.ISSUED,
                        expirationTime,
                        "userId"
                ));

        AuthorizationCodeId authorizationCodeId = AuthorizationCodeId.of("id");

        // when
        sut.update(authorizationCodeId, AuthorizationCode::markAsUsed);

        // then
        AuthorizationCodeJpaEntity jpaEntity = authorizationCodeJpaRepository.findById(
                authorizationCodeId.getValue()).get();
        assertThat(jpaEntity.getStatus()).isEqualByComparingTo(AuthorizationCodeStatus.USED);
    }
}