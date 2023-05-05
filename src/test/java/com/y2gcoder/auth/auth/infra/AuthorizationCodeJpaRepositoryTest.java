package com.y2gcoder.auth.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.auth.AuthIntegrationTestSupport;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AuthorizationCodeJpaRepositoryTest extends AuthIntegrationTestSupport {

    @Autowired
    private AuthorizationCodeJpaRepository sut;

    @DisplayName("code로 인증 코드 JPA 엔티티를 조회할 수 있다.")
    @Test
    void findByCode() {
        // given
        String targetCode = "code";
        LocalDateTime expirationTime = LocalDateTime.of(2023, 5, 4, 14, 25);
        AuthorizationCodeJpaEntity authorizationCodeJpaEntity = sut.save(
                new AuthorizationCodeJpaEntity(
                        "id",
                        targetCode,
                        AuthorizationCodeStatus.ISSUED,
                        expirationTime,
                        "userId"
                ));

        // when
        Optional<AuthorizationCodeJpaEntity> result = sut.findByCode(targetCode);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(authorizationCodeJpaEntity);
    }
}