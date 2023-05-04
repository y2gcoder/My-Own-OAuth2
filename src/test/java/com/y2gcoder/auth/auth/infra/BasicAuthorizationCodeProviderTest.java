package com.y2gcoder.auth.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.auth.domain.AuthorizationCodeProvider;
import com.y2gcoder.auth.common.infra.StubTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BasicAuthorizationCodeProviderTest {

    private AuthorizationCodeProvider sut;

    @DisplayName("매번 다른 code를 생성한다.")
    @Test
    void generateCode() {
        // given
        LocalDateTime fixedLocalDateTime = LocalDateTime
                .of(2023, 5, 4, 13, 19);

        sut = new BasicAuthorizationCodeProvider(new StubTime(fixedLocalDateTime),
                Duration.ofMinutes(5));
        Set<String> codes = new HashSet<>();

        // when
        for (int i = 0; i < 10; i++) {
            codes.add(sut.generateCode());
        }

        // then
        assertThat(codes).hasSize(10);
    }

    @DisplayName("코드의 만료일자를 생성한다.")
    @Test
    void getExpirationTime() {
        // given
        LocalDateTime fixedLocalDateTime = LocalDateTime
                .of(2023, 5, 4, 13, 19);

        Duration expiration = Duration.ofMinutes(5);
        sut = new BasicAuthorizationCodeProvider(new StubTime(fixedLocalDateTime),
                expiration);

        // when
        LocalDateTime result = sut.getExpirationTime();

        // then
        assertThat(result).isEqualTo(fixedLocalDateTime.plus(expiration));
    }
}