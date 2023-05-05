package com.y2gcoder.auth.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RefreshTokenJpaRepositoryTest {

    @Autowired
    private RefreshTokenJpaRepository sut;

    @DisplayName("소유자 ID로 가장 최근 발행한 리프레시 토큰을 가져올 수 있다.")
    @Test
    void findFirstByOwnerIdOrderByIssuedAtDesc() {
        // given
        UserId ownerId = UserId.of("ownerId");
        List<RefreshTokenJpaEntity> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LocalDateTime issuedAt = LocalDateTime.of(2023, 5, 5, 15, 17, i);
            LocalDateTime expirationTime = issuedAt.plusDays(1);
            entities.add(new RefreshTokenJpaEntity(
                    "id" + i,
                    "token" + i,
                    ownerId.getValue(),
                    expirationTime,
                    issuedAt
            ));
        }
        sut.saveAll(entities);

        // when
        Optional<RefreshTokenJpaEntity> result = sut.findFirstByOwnerIdOrderByIssuedAtDesc(ownerId.getValue());

        // then

        assertThat(result).isPresent();
        RefreshTokenJpaEntity actual = result.get();
        RefreshTokenJpaEntity expected = entities.get(9);
        assertThat(actual)
                .extracting("id", "token", "ownerId", "issuedAt", "expirationTime")
                .contains(
                        expected.getId(),
                        expected.getToken(),
                        expected.getOwnerId(),
                        expected.getIssuedAt(),
                        expected.getExpirationTime()
                );
    }

}