package com.y2gcoder.auth.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RefreshTokenTest {

    private static Stream<Arguments> provideReferenceTimeAndExpirationTimeAndExpected() {
        return Stream.of(
                Arguments.of(
                    LocalDateTime.of(2023, 5, 5, 22, 9, 0),
                    LocalDateTime.of(2023, 5, 5, 22, 9, 1),
                        false
                ),
                Arguments.of(
                        LocalDateTime.of(2023, 5, 5, 22, 9, 5),
                        LocalDateTime.of(2023, 5, 5, 22, 9, 0),
                        true
                )
        );
    }

    @DisplayName("기준시간을 받아서 리프레시 토큰이 만료되었는지 확인할 수 있다.")
    @MethodSource("provideReferenceTimeAndExpirationTimeAndExpected")
    @ParameterizedTest
    void isExpiredAt(LocalDateTime referenceTime, LocalDateTime expirationTime, boolean expected) {
        // given
        LocalDateTime issuedAt = LocalDateTime.of(2023, 5, 5, 22, 0);
        RefreshToken refreshToken = new RefreshToken(
                RefreshTokenId.of("id"),
                "token",
                UserId.of("ownerId"),
                expirationTime,
                issuedAt
        );

        // when
        boolean result = refreshToken.isExpiredAt(referenceTime);

        // then
        assertThat(result).isEqualTo(expected);
    }
}