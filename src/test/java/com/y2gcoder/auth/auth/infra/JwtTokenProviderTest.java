package com.y2gcoder.auth.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {


    private String secretString;
    private Duration expiration;
    private JwtTokenProvider sut;

    @DisplayName("JWT 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofSeconds(5);
        sut = new JwtTokenProvider(secretString, expiration);

        String username = String.valueOf(1L);

        //when
        String result = sut.generateToken(username);

        //then
        sut.validateToken(result);
    }

    @DisplayName("유효기간이 지난 토큰을 검증할 수 있다.")
    @Test
    void validateTokenWithExpired() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ZERO;
        sut = new JwtTokenProvider(secretString, expiration);
        String username = String.valueOf(1L);
        String result = sut.generateToken(username);

        //expected
        assertThatThrownBy(() -> sut.validateToken(result))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @DisplayName("잘못된 토큰을 검증할 수 있다.")
    @Test
    void validateTokenWithInvalidJwtToken() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofMillis(5 * 1000L);
        sut = new JwtTokenProvider(secretString, expiration);

        String wrongToken = "wrongToken";

        //expected
        assertThatThrownBy(() -> sut.validateToken(wrongToken))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    @DisplayName("다른 키로 서명한 토큰을 검증할 수 있다.")
    void validateTokenWithGeneratedDifferentSignature() {
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
        assertThatThrownBy(() -> sut.validateToken(token))
                .isInstanceOf(SignatureException.class);
    }

    @DisplayName("토큰에서 username을 조회할 수 있다.")
    @Test
    void getUsernameFrom() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofSeconds(5);
        sut = new JwtTokenProvider(secretString, expiration);
        String username = String.valueOf(1L);
        String token = sut.generateToken(username);

        //when
        String result = sut.getUsernameFrom(token);

        //when
        assertThat(result).isEqualTo(username);
    }

    @Test
    @DisplayName("만료된 토큰에서 username을 조회할 수 있다.")
    void getUsernameFromExpired() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ZERO;
        sut = new JwtTokenProvider(secretString, expiration);
        String username = String.valueOf(1L);
        String token = sut.generateToken(username);

        //when
        String result = sut.getUsernameFrom(token);

        //when
        assertThat(result).isEqualTo(username);
    }

    @Test
    @DisplayName("잘못된 토큰에서 username을 가져올 수 없다.")
    void getUsernameFromWrong() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofMillis(5 * 1000L);
        sut = new JwtTokenProvider(secretString, expiration);
        String wrongToken = "wrongToken";

        //expected
        assertThatThrownBy(() -> sut.getUsernameFrom(wrongToken))
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    @DisplayName("다른 키로 서명한 토큰에서 username을 가져올 수 없다.")
    void getUsernameFromDifferentSignature() {
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
        assertThatThrownBy(() -> sut.getUsernameFrom(token))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("토큰에서 만료시간을 가져올 수 있다.")
    void getExpiration() {
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
        LocalDateTime issuedDateTime = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody().getIssuedAt().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        assertThat(result).isEqualToIgnoringNanos(issuedDateTime.plusSeconds(60));
    }

    @DisplayName("만료된 토큰에서 만료시간을 가져올 수 없다.")
    @Test
    void getExpirationWithExpired() {
        //given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ZERO;
        sut = new JwtTokenProvider(secretString, expiration);
        String username = String.valueOf(1L);
        String token = sut.generateToken(username);

        //expected
        assertThatThrownBy(() -> sut.getExpiration(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @DisplayName("잘못된 토큰에서 만료시간을 가져올 수 없다.")
    @Test
    void getExpirationWithWrong() {
        // given
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        expiration = Duration.ofSeconds(5);
        sut = new JwtTokenProvider(secretString, expiration);

        String token = "wrong";

        // expected
        assertThatThrownBy(() -> sut.getExpiration(token))
                .isInstanceOf(MalformedJwtException.class);
    }

    @DisplayName("다른 키로 서명한 토큰에서 만료시간을 가져올 수 없다.")
    @Test
    void getExpirationWithDifferentSignature() {
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
        assertThatThrownBy(() -> sut.getExpiration(token))
                .isInstanceOf(SignatureException.class);

    }
}