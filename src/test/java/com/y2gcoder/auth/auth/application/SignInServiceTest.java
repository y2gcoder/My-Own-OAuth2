package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.*;
import com.y2gcoder.auth.auth.infra.FakeAuthorizationCodeRepository;
import com.y2gcoder.auth.auth.infra.FakeRefreshTokenRepository;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.auth.infra.StubRefreshTokenProvider;
import com.y2gcoder.auth.user.domain.UserId;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignInServiceTest {
    private AuthorizationCodeRepository authorizationCodeRepository;
    private JwtTokenProvider jwtTokenProvider;
    private RefreshTokenRepository refreshTokenRepository;
    private RefreshTokenProvider refreshTokenProvider;
    private SignInService sut;

    @BeforeEach
    void setUp() {
        authorizationCodeRepository = new FakeAuthorizationCodeRepository();

        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secretString = Encoders.BASE64.encode(key.getEncoded());
        Duration expiration = Duration.ofMinutes(15L);
        jwtTokenProvider = new JwtTokenProvider(secretString, expiration);
        refreshTokenRepository = new FakeRefreshTokenRepository();
        refreshTokenProvider = new StubRefreshTokenProvider();
        sut = new SignInService(
                authorizationCodeRepository,
                jwtTokenProvider,
                refreshTokenRepository,
                refreshTokenProvider
        );
    }

    @Test
    @DisplayName("인증 코드로 로그인 할 수 있다.")
    void givenAuthorizationCode_whenSignIn_thenSignInResponseIsReturned() {
        //given
        AuthorizationCodeId authorizationCodeId = authorizationCodeRepository.nextAuthorizationCodeId();
        AuthorizationCode authorizationCode = new AuthorizationCode(authorizationCodeId,
                "authorizationcode",
                LocalDateTime.now().plusMinutes(5L),
                AuthorizationCodeStatus.ISSUED,
                UserId.of("ownerId"));
        authorizationCodeRepository.save(authorizationCode);

        //when
        String code = authorizationCode.getCode();
        SignInDto result = sut.signIn(code);

        //then
        jwtTokenProvider.validateToken(result.getAccess().getToken());
        Optional<AuthorizationCode> optionalAuthorizationCode = authorizationCodeRepository.findByCode(code);
        assertThat(optionalAuthorizationCode.isPresent()).isTrue();
        AuthorizationCode resultAuthorizationCode = optionalAuthorizationCode.get();
        assertThat(resultAuthorizationCode.isUsed()).isTrue();

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByToken(result.getRefresh().getToken());
        assertThat(optionalRefreshToken.isPresent()).isTrue();
        assertThat(optionalRefreshToken.get().getToken()).isEqualTo(result.getRefresh().getToken());
    }

    @Test
    @DisplayName("만료된 인증 코드로 로그인할 수 없다.")
    void givenExpiredAuthorizationCode_whenSignIn_thenExceptionShouldBeThrown() {
        //given
        AuthorizationCodeId authorizationCodeId = authorizationCodeRepository.nextAuthorizationCodeId();
        AuthorizationCode authorizationCode = new AuthorizationCode(authorizationCodeId,
                "authorizationcode",
                LocalDateTime.now(),
                AuthorizationCodeStatus.ISSUED,
                UserId.of("ownerId"));
        authorizationCodeRepository.save(authorizationCode);

        //expected
        String code = authorizationCode.getCode();
        assertThatThrownBy(() -> sut.signIn(code))
                .isInstanceOf(UnavailableAuthorizationCodeException.class);
    }

    @Test
    @DisplayName("이미 사용한 인증 코드로 로그인할 수 없다.")
    void givenUsedAuthorizationCode_whenSignIn_thenExceptionShouldBeThrown() {
        //given
        AuthorizationCodeId authorizationCodeId = authorizationCodeRepository.nextAuthorizationCodeId();
        AuthorizationCode authorizationCode = new AuthorizationCode(authorizationCodeId,
                "authorizationcode",
                LocalDateTime.now().plusMinutes(5L),
                AuthorizationCodeStatus.USED,
                UserId.of("ownerId"));
        authorizationCodeRepository.save(authorizationCode);

        //expected
        String code = authorizationCode.getCode();
        assertThatThrownBy(() -> sut.signIn(code))
                .isInstanceOf(UnavailableAuthorizationCodeException.class);
    }
}