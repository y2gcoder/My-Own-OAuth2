package com.y2gcoder.auth.auth.domain;

import com.y2gcoder.auth.user.domain.UserId;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationCodeTest {

    private static Stream<Arguments> provideExpirationTimesForIsExpired() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().plusDays(1), false),
                Arguments.of(LocalDateTime.now().minusMinutes(1), true)
        );
    }

    @DisplayName("인증 코드가 만료되었는지 확인한다.")
    @MethodSource("provideExpirationTimesForIsExpired")
    @ParameterizedTest
    void isExpired(LocalDateTime expirationTime, boolean expected) {
        // given
        AuthorizationCode authorizationCode = new AuthorizationCode(
                AuthorizationCodeId.of("authcodeid"),
                "authorizationcode",
                expirationTime,
                AuthorizationCodeStatus.ISSUED,
                UserId.of("userid")
        );

        // when
        boolean result = authorizationCode.isExpired();

        // then
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideAuthorizationCodeStatusForIsUsed() {
        return Stream.of(
                Arguments.of(AuthorizationCodeStatus.ISSUED, false),
                Arguments.of(AuthorizationCodeStatus.USED, true)
        );
    }

    @DisplayName("인증 코드가 사용된 상태인지 확인할 수 있다.")
    @MethodSource("provideAuthorizationCodeStatusForIsUsed")
    @ParameterizedTest
    void isUsed(AuthorizationCodeStatus status, boolean expected) {
        // given
        LocalDateTime expirationTime = LocalDateTime.of(2023, 5, 4, 9, 18);
        AuthorizationCode authorizationCode = new AuthorizationCode(
                AuthorizationCodeId.of("authcodeid"),
                "authorizationcode",
                expirationTime,
                status,
                UserId.of("userid")
        );

        // when
        boolean result = authorizationCode.isUsed();

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("인증 코드를 사용한 상태로 변경할 수 있다.")
    @Test
    void markAsUsed() {
        // given
        LocalDateTime expirationTime = LocalDateTime.of(2023, 5, 4, 9, 18);
        AuthorizationCode authorizationCode = new AuthorizationCode(
                AuthorizationCodeId.of("authcodeid"),
                "authorizationcode",
                expirationTime,
                AuthorizationCodeStatus.ISSUED,
                UserId.of("userid")
        );

        assertThat(authorizationCode.isUsed()).isFalse();

        // when
        authorizationCode.markAsUsed();

        // then
        assertThat(authorizationCode.isUsed()).isTrue();

    }

    @DisplayName("사용할 수 있는 인증 코드인지 확인한다.")
    @Test
    void isAvailable() {
        // given
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(1);
        AuthorizationCode authorizationCode = new AuthorizationCode(
                AuthorizationCodeId.of("authcodeid"),
                "authorizationcode",
                expirationTime,
                AuthorizationCodeStatus.ISSUED,
                UserId.of("userid")
        );

        // when // then
        assertThat(authorizationCode.isAvailable()).isTrue();
    }

    @DisplayName("만료된 인증 코드는 사용할 수 없다.")
    @Test
    void isAvailableWithExpired() {
        // given
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(1);
        AuthorizationCode authorizationCode = new AuthorizationCode(
                AuthorizationCodeId.of("authcodeid"),
                "authorizationcode",
                expirationTime,
                AuthorizationCodeStatus.ISSUED,
                UserId.of("userid")
        );

        // when // then
        assertThat(authorizationCode.isAvailable()).isFalse();
    }

    @DisplayName("이미 사용한 인증코드는 사용할 수 없다.")
    @Test
    void isAvailableWithUsed() {
        // given
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(1);
        AuthorizationCode authorizationCode = new AuthorizationCode(
                AuthorizationCodeId.of("authcodeid"),
                "authorizationcode",
                expirationTime,
                AuthorizationCodeStatus.USED,
                UserId.of("userid")
        );

        // when // then
        assertThat(authorizationCode.isAvailable()).isFalse();

    }


}