package com.y2gcoder.auth.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.y2gcoder.auth.auth.AuthIntegrationTestSupport;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.auth.infra.RefreshTokenJpaEntity;
import com.y2gcoder.auth.auth.infra.RefreshTokenJpaRepository;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CreateTokenServiceTest extends AuthIntegrationTestSupport {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Autowired
    private CreateTokenService sut;

    @AfterEach
    void tearDown() {
        refreshTokenJpaRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 ID로 액세스 토큰을 발급할 수 있다.")
    @Test
    void createAccessToken() {
        // given
        UserId ownerId = UserId.of("userId");
        LocalDateTime currentTime = LocalDateTime.now();

        // when
        AccessTokenDto result = sut.createAccessToken(ownerId, currentTime);

        // then
        String username = jwtTokenProvider.getUsernameFrom(result.getToken());
        assertThat(username).isEqualTo(ownerId.getValue());
    }

    @DisplayName("사용자 ID로 리프레시 토큰을 발급할 수 있다.")
    @Test
    void createRefreshToken() {
        // given
        UserId ownerId = UserId.of("userId");
        LocalDateTime currentTime = LocalDateTime.now();

        // when
        RefreshTokenDto result = sut.createRefreshToken(ownerId, currentTime);

        // then
        List<RefreshTokenJpaEntity> jpaEntities = refreshTokenJpaRepository.findAll();
        assertThat(jpaEntities).hasSize(1)
                .extracting("token", "expirationTime")
                .containsExactlyInAnyOrder(
                        tuple(result.getToken(), result.getExpirationTime())
                );

    }
}