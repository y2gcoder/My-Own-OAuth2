package com.y2gcoder.auth.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.y2gcoder.auth.auth.domain.RefreshTokenId;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.auth.infra.RefreshTokenJpaEntity;
import com.y2gcoder.auth.auth.infra.RefreshTokenJpaRepository;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TokenRefreshServiceTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Autowired
    private TokenRefreshService sut;

    @AfterEach
    void tearDown() {
        refreshTokenJpaRepository.deleteAllInBatch();
    }

    @DisplayName("액세스 토큰과 리프레시 토큰으로 토큰을 재발급한다.")
    @Test
    void tokenRefresh() {
        // given
        UserId ownerId = UserId.of("ownerId");
        LocalDateTime currentTime = LocalDateTime.now();
        String oldAccessToken = jwtTokenProvider.generateToken(ownerId.getValue(), currentTime);

        RefreshTokenId refreshTokenId = RefreshTokenId.of("refreshtokenid");
        RefreshTokenJpaEntity refreshToken = refreshTokenJpaRepository.save(
                new RefreshTokenJpaEntity(
                        refreshTokenId.getValue(),
                        "token",
                        ownerId.getValue(),
                        currentTime.plusMinutes(30),
                        currentTime
                ));

        // when
        TokenRefreshDto result = sut.tokenRefresh(oldAccessToken, refreshToken.getToken(),
                currentTime);

        // then
        assertThat(result).isNotNull();
        RefreshTokenDto refreshTokenDto = result.getRefresh();
        assertThat(refreshTokenDto.getToken()).isEqualTo(refreshToken.getToken());
        assertThat(refreshTokenDto.getExpirationTime()).isEqualTo(refreshToken.getExpirationTime());

        AccessTokenDto accessTokenDto = result.getAccess();
        assertThat(accessTokenDto.getToken()).isNotEqualTo(oldAccessToken);
    }

    @DisplayName("액세스 토큰이 만료되었어도 토큰을 재발급할 수 있다.")
    @Test
    void tokenRefreshWithExpiredAccessToken() {
        // given
        UserId ownerId = UserId.of("ownerId");
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiredTime = currentTime.minusYears(1);
        String oldAccessToken = jwtTokenProvider.generateToken(ownerId.getValue(), expiredTime);

        RefreshTokenId refreshTokenId = RefreshTokenId.of("refreshtokenid");
        RefreshTokenJpaEntity refreshToken = refreshTokenJpaRepository.save(
                new RefreshTokenJpaEntity(
                        refreshTokenId.getValue(),
                        "token",
                        ownerId.getValue(),
                        currentTime.plusMinutes(30),
                        currentTime
                ));

        // when
        TokenRefreshDto result = sut.tokenRefresh(oldAccessToken, refreshToken.getToken(),
                currentTime);

        // then
        assertThat(result).isNotNull();
        RefreshTokenDto refreshTokenDto = result.getRefresh();
        assertThat(refreshTokenDto.getToken()).isEqualTo(refreshToken.getToken());
        assertThat(refreshTokenDto.getExpirationTime()).isEqualTo(refreshToken.getExpirationTime());

        AccessTokenDto accessTokenDto = result.getAccess();
        assertThat(accessTokenDto.getToken()).isNotEqualTo(oldAccessToken);
    }

    @DisplayName("유효하지 않은 액세스 토큰으로는 토큰을 재발급할 수 없다.")
    @Test
    void tokenRefreshWithInvalidAccessToken() {
        // given
        UserId ownerId = UserId.of("ownerId");
        LocalDateTime currentTime = LocalDateTime.now();
        String oldAccessToken = "invalid";

        RefreshTokenId refreshTokenId = RefreshTokenId.of("refreshtokenid");
        RefreshTokenJpaEntity refreshToken = refreshTokenJpaRepository.save(
                new RefreshTokenJpaEntity(
                        refreshTokenId.getValue(),
                        "token",
                        ownerId.getValue(),
                        currentTime.plusMinutes(30),
                        currentTime
                ));

        // expected
        assertThatThrownBy(() -> sut.tokenRefresh(oldAccessToken, refreshToken.getToken(),
                currentTime))
                .isInstanceOf(InvalidAccessTokenException.class)
                .hasMessage("유효하지 않은 액세스 토큰입니다.");
    }

    @DisplayName("소유자의 리프레시 토큰이 존재하지 않는다면 토큰을 재발급할 수 없다.")
    @Test
    void tokenRefreshWithNotFoundRefreshToken() {
        // given
        UserId ownerId = UserId.of("ownerId");
        LocalDateTime currentTime = LocalDateTime.now();
        String oldAccessToken = jwtTokenProvider.generateToken(ownerId.getValue(), currentTime);

        // expected
        assertThatThrownBy(() -> sut.tokenRefresh(oldAccessToken, "refresh",
                currentTime))
                .isInstanceOf(NotFoundRefreshTokenException.class)
                .hasMessage("리프레시 토큰을 찾을 수 없습니다.");

    }

    @DisplayName("입력한 리프레시 토큰과 소유자가 가장 최근에 발행한 리프레시 토큰이 일치하지 않으면 토큰을 재발급할 수 없다.")
    @Test
    void tokenRefreshWhenInputRefreshTokenIsNotEqualToFoundRefreshToken() {
        // given
        UserId ownerId = UserId.of("ownerId");
        LocalDateTime currentTime = LocalDateTime.now();
        String oldAccessToken = jwtTokenProvider.generateToken(ownerId.getValue(), currentTime);

        RefreshTokenId refreshTokenId = RefreshTokenId.of("refreshtokenid");
        RefreshTokenJpaEntity refreshToken = refreshTokenJpaRepository.save(
                new RefreshTokenJpaEntity(
                        refreshTokenId.getValue(),
                        "token",
                        ownerId.getValue(),
                        currentTime.plusMinutes(30),
                        currentTime
                ));

        // expected
        assertThatThrownBy(() -> sut.tokenRefresh(oldAccessToken, "mismatch",
                currentTime))
                .isInstanceOf(RefreshTokenMismatchException.class)
                .hasMessage("리프레시 토큰 불일치: 요청된 리프레시 토큰과 저장된 리프레시 토큰이 일치하지 않습니다.");

    }

    @DisplayName("리프레시 토큰이 만료되었으면 토큰을 재발급할 수 없다.")
    @Test
    void tokenRefreshWithExpiredRefreshToken() {
        // given
        // given
        UserId ownerId = UserId.of("ownerId");
        LocalDateTime currentTime = LocalDateTime.now();
        String oldAccessToken = jwtTokenProvider.generateToken(ownerId.getValue(), currentTime);

        RefreshTokenId refreshTokenId = RefreshTokenId.of("refreshtokenid");
        RefreshTokenJpaEntity refreshToken = refreshTokenJpaRepository.save(
                new RefreshTokenJpaEntity(
                        refreshTokenId.getValue(),
                        "token",
                        ownerId.getValue(),
                        currentTime,
                        currentTime
                ));

        // expected
        assertThatThrownBy(() -> sut.tokenRefresh(oldAccessToken, refreshToken.getToken(),
                currentTime))
                .isInstanceOf(ExpiredRefreshTokenException.class)
                .hasMessage("리프레시 토큰이 만료되었습니다.");

    }

}