package com.y2gcoder.auth.auth.domain;

import com.y2gcoder.auth.user.domain.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationCodeTest {

    @Test
    @DisplayName("만료되지 않고, 사용하지 않은 인증 코드는 사용할 수 있다.")
    void givenAuthorizationCode_whenIsAvailable_thenTrueIsReturned() {
        //given
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
        AuthorizationCode authorizationCode = new AuthorizationCode(
                AuthorizationCodeId.of("authcodeid"),
                "authorizationcode",
                expirationTime,
                AuthorizationCodeStatus.ISSUED,
                UserId.of("userid")
        );

        //expected
        assertThat(authorizationCode.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("만료된 인증 코드는 사용할 수 없다.")
    void givenAuthorizationCodeIsExpired_whenIsAvailable_thenFalseIsReturned() {
        //given
        LocalDateTime expirationTime = LocalDateTime.now();
        AuthorizationCode authorizationCode = new AuthorizationCode(
                AuthorizationCodeId.of("authcodeid"),
                "authorizationcode",
                expirationTime,
                AuthorizationCodeStatus.ISSUED,
                UserId.of("userid")
        );

        //expected
        assertThat(authorizationCode.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("사용한 인증 코드는 다시 사용할 수 없다.")
    void givenAuthorizationCodeIsUsed_whenIsAvailable_thenFalseIsReturned() {
        //given
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
        AuthorizationCode authorizationCode = new AuthorizationCode(
                AuthorizationCodeId.of("authcodeid"),
                "authorizationcode",
                expirationTime,
                AuthorizationCodeStatus.ISSUED,
                UserId.of("userid")
        );

        //when
        authorizationCode.markAsUsed();

        //then
        assertThat(authorizationCode.isAvailable()).isFalse();
    }

}