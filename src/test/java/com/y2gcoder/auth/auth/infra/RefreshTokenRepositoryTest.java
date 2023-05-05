package com.y2gcoder.auth.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.auth.application.RefreshTokenRepository;
import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.domain.RefreshTokenId;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Autowired
    private RefreshTokenRepository sut;

    @AfterEach
    void tearDown() {
        refreshTokenJpaRepository.deleteAllInBatch();
    }

    @DisplayName("매번 새로운 리프레시 토큰 ID를 생성한다.")
    @Test
    void nextRefreshTokenId() {
        // given
        Set<RefreshTokenId> refreshTokenIds = new HashSet<>();

        // when
        for (int i = 0; i < 5; i++) {
            refreshTokenIds.add(sut.nextRefreshTokenId());
        }

        // then
        assertThat(refreshTokenIds).hasSize(5);
    }

    @DisplayName("리프레시 토큰 엔티티를 저장할 수 있다.")
    @Test
    void save() {
        // given
        RefreshTokenId refreshTokenId = RefreshTokenId.of("id");
        LocalDateTime issuedAt = LocalDateTime.of(2023, 5, 4, 15, 55);
        LocalDateTime expirationTime = issuedAt.plusMinutes(5);
        RefreshToken refreshToken = new RefreshToken(
                refreshTokenId,
                "token",
                UserId.of("ownerId"),
                expirationTime,
                issuedAt
        );

        // when
        RefreshToken result = sut.save(refreshToken);

        // then
        assertThat(result).isEqualTo(refreshToken);
        assertThat(result.getIssuedAt()).isEqualTo(refreshToken.getIssuedAt());
        assertThat(result.getExpirationTime()).isEqualTo(refreshToken.getExpirationTime());

        List<RefreshTokenJpaEntity> refreshTokenJpaEntities = refreshTokenJpaRepository.findAll();
        assertThat(refreshTokenJpaEntities).hasSize(1)
                .extracting("id", "token", "ownerId")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("id", "token", "ownerId")
                );
    }

    @DisplayName("소유자 ID로 가장 최근에 발행한 리프레시 토큰을 가져올 수 있다.")
    @Test
    void findFirstByOwnerIdOrderByIssuedAtDesc() {
        // given
        UserId ownerId = UserId.of("ownerId");
        List<RefreshTokenJpaEntity> jpaEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LocalDateTime issuedAt = LocalDateTime.of(2023, 5, 5, 15, 17, i);
            LocalDateTime expirationTime = issuedAt.plusDays(1);
            jpaEntities.add(new RefreshTokenJpaEntity(
                    "id" + i,
                    "token" + i,
                    ownerId.getValue(),
                    expirationTime,
                    issuedAt
            ));
        }
        refreshTokenJpaRepository.saveAll(jpaEntities);

        // when
        Optional<RefreshToken> result = sut.findLatestRefreshTokenByOwnerId(
                ownerId);

        // then
        assertThat(result).isPresent();
        RefreshToken actual = result.get();
        RefreshTokenJpaEntity expected = jpaEntities.get(9);
        assertThat(actual)
                .extracting("id", "token", "ownerId", "issuedAt", "expirationTime")
                .contains(
                        RefreshTokenId.of(expected.getId()),
                        expected.getToken(),
                        UserId.of(expected.getOwnerId()),
                        expected.getIssuedAt(),
                        expected.getExpirationTime()
                );
    }

}