package com.y2gcoder.auth.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.auth.domain.RefreshTokenProvider;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RefreshTokenProviderTest {

    private RefreshTokenProvider sut;

    @BeforeEach
    void setUp() {
        Duration expiration = Duration.ofMinutes(5);
        sut = new RefreshTokenProviderImpl(expiration);
    }

    @DisplayName("매번 새로운 refresh token을 생성한다.")
    @Test
    void generateToken() {
        // given
        Set<String> tokens = new HashSet<>();

        // when
        for (int i = 0; i < 10; i++) {
            tokens.add(sut.generateToken());
        }

        // then
        assertThat(tokens).hasSize(10);
    }

    @DisplayName("토큰의 만료일자를 만든다.")
    @Test
    void getExpirationTime() {
        //given
        LocalDateTime currentTime = LocalDateTime.of(2023, 5, 4, 15, 46);

        // when

        LocalDateTime result = sut.getExpirationTime(currentTime);

        // then
        assertThat(result).isEqualTo(currentTime.plus(Duration.ofMinutes(5)));

    }

}