package com.y2gcoder.auth.auth.infra;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {


    private String secretString;
    private Duration expiration;
    private JwtTokenProvider sut;


    @Test
    @DisplayName("JWT 토큰을 만들 수 있다.")
    void whenGenerateToken_thenJwtTokenIsReturned() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofMillis(5 * 1000L);
        sut = new JwtTokenProvider(secretString, expiration);

        //when
        String username = String.valueOf(1L);
        String result = sut.generateToken(username);


        //then
        sut.validateToken(result);
    }

    @Test
    @DisplayName("유효기간이 지난 토큰을 검증할 수 있다.")
    void givenExpiredToken_whenValidateToken_thenExceptionShouldBeThrown() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofMillis(1L);
        sut = new JwtTokenProvider(secretString, expiration);
        String username = String.valueOf(1L);
        String result = sut.generateToken(username);

        //expected
        Assertions.assertThatThrownBy(() -> sut.validateToken(result))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("잘못된 형식의 토큰을 검증할 수 있다.")
    void givenInvalidJwtToken_whenValidateToken_thenExceptionShouldBeThrown() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofMillis(5 * 1000L);
        sut = new JwtTokenProvider(secretString, expiration);

        //expected
        String wrongToken = "wrongToken";
        Assertions.assertThatThrownBy(() -> sut.validateToken(wrongToken))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    @DisplayName("다른 키로 만든 토큰을 검증할 수 있다.")
    void givenJwtTokenWithDifferentKey_whenValidateToken_thenExceptionShouldBeThrown() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofMillis(5 * 1000L);
        sut = new JwtTokenProvider(secretString, expiration);

        String username = String.valueOf(1L);
        String token = sut.generateToken(username);

        SecretKey anotherKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String newSecretString = Encoders.BASE64.encode(anotherKey.getEncoded());
        sut = new JwtTokenProvider(newSecretString, expiration);

        //expected
        Assertions.assertThatThrownBy(() -> sut.validateToken(token))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("토큰에서 username을 조회할 수 있다.")
    void givenValidToken_whenGetUsernameFromToken_thenUsernameIsReturned() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofMillis(5 * 1000L);
        sut = new JwtTokenProvider(secretString, expiration);
        String username = String.valueOf(1L);
        String token = sut.generateToken(username);

        //when
        String result = sut.getUsernameFromToken(token);

        //when
        assertThat(result).isEqualTo(username);
    }

    @Test
    @DisplayName("토큰에서 만료날짜를 가져올 수 있다.")
    void givenValidToken_whenGetExpiration_thenExpirationDateIsReturned() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofSeconds(60L);
        sut = new JwtTokenProvider(secretString, expiration);
        String username = String.valueOf(1L);
        String token = sut.generateToken(username);

        //when
        LocalDateTime result = sut.getExpiration(token);

        //then
        LocalDateTime issuedDateTime = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody().getIssuedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        assertThat(result).isEqualToIgnoringNanos(issuedDateTime.plusSeconds(60));
    }

    @Test
    @DisplayName("만료된 토큰에서 username을 조회할 수 있다.")
    void givenExpiredToken_whenGetUsernameFromToken_thenUsernameIsReturned() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ZERO;
        sut = new JwtTokenProvider(secretString, expiration);
        String username = String.valueOf(1L);
        String token = sut.generateToken(username);

        //when
        String result = sut.getUsernameFromToken(token);

        //when
        assertThat(result).isEqualTo(username);
    }

    @Test
    @DisplayName("다른 키로 만든 토큰에서 username을 가져올 수 없다.")
    void givenDifferentKey_whenGetUsernameFromToken_thenExceptionShouldBeThrown() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofMillis(5 * 1000L);
        sut = new JwtTokenProvider(secretString, expiration);

        String username = String.valueOf(1L);
        String token = sut.generateToken(username);

        SecretKey anotherKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String newSecretString = Encoders.BASE64.encode(anotherKey.getEncoded());
        sut = new JwtTokenProvider(newSecretString, expiration);

        //expected
        Assertions.assertThatThrownBy(() -> sut.getUsernameFromToken(token))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("잘못된 토큰에서 username을 가져올 수 없다.")
    void givenInvalidToken_whenGetUsernameFromToken_thenExceptionShouldBeThrown() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofMillis(5 * 1000L);
        sut = new JwtTokenProvider(secretString, expiration);

        //expected
        String wrongToken = "wrongToken";
        Assertions.assertThatThrownBy(() -> sut.getUsernameFromToken(wrongToken))
                .isInstanceOf(MalformedJwtException.class);
    }
}