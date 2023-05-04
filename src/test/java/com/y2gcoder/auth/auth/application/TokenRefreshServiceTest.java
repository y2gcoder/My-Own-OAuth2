package com.y2gcoder.auth.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

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

    @DisplayName("액세스 토큰과 리프레시 토큰으로 액세스 토큰을 재발급한다.")
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

    //    @Test
//    @DisplayName("액세스 토큰과 리프레시 토큰으로 새로운 액세스 토큰을 발급할 수 있다.")
//    void givenAccessTokenAndRefreshToken_whenTokenRefresh_thenNewAccessTokenIsReturned() throws InterruptedException {
//        //given
//        UserId ownerId = UserId.of("ownerId");
//
//        String accessToken = jwtTokenProvider.generateToken(ownerId.getValue());
//
//        RefreshTokenId refreshTokenId = refreshTokenRepository.nextRefreshTokenId();
//        LocalDateTime refreshTokenIssuedAt = LocalDateTime.now();
//        LocalDateTime refreshTokenExpirationTime = refreshTokenIssuedAt.plusDays(30);
//        RefreshToken refreshToken = new RefreshToken(
//                refreshTokenId,
//                "refreshtoken",
//                ownerId,
//                refreshTokenExpirationTime,
//                refreshTokenIssuedAt
//        );
//        refreshTokenRepository.save(refreshToken);
//
//        //when
//        TokenRefreshDto result = sut.tokenRefresh(accessToken, refreshToken.getToken());
//
//        //then
//        Assertions.assertThat(result.getAccess().getToken()).isNotEqualTo(accessToken);
//    }
//
//    @Test
//    @DisplayName("만료된 액세스 토큰과 리프레시 토큰으로 새로운 액세스 토큰을 발급할 수 있다.")
//    void givenExpiredAccessTokenAndRefreshToken_whenTokenRefresh_thenNewAccessTokenIsReturned() {
//        //given
//        UserId ownerId = UserId.of("ownerId");
//
//        Duration zeroExpiration = Duration.ZERO;
//        JwtTokenProvider expiredJwtTokenProvider = new JwtTokenProvider(secretString, zeroExpiration);
//        String accessToken = expiredJwtTokenProvider.generateToken(ownerId.getValue());
//
//        RefreshTokenId refreshTokenId = refreshTokenRepository.nextRefreshTokenId();
//        LocalDateTime refreshTokenIssuedAt = LocalDateTime.now();
//        LocalDateTime refreshTokenExpirationTime = refreshTokenIssuedAt.plusDays(30);
//        RefreshToken refreshToken = new RefreshToken(
//                refreshTokenId,
//                "refreshtoken",
//                ownerId,
//                refreshTokenExpirationTime,
//                refreshTokenIssuedAt
//        );
//        refreshTokenRepository.save(refreshToken);
//
//        //when
//        TokenRefreshDto result = sut.tokenRefresh(accessToken, refreshToken.getToken());
//
//        //then
//        Assertions.assertThat(result.getAccess().getToken()).isNotEqualTo(accessToken);
//
//    }
//
//    @Test
//    @DisplayName("해당 사용자의 리프레시 토큰을 찾지 못하면 토큰을 재발급할 수 없다.")
//    void givenNotFoundRefreshTokenByOwnerId_whenTokenRefresh_thenExceptionShouldBeThrown() {
//        //given
//        UserId anotherOwnerId = UserId.of("anotherOwnerId");
//
//        String accessToken = jwtTokenProvider.generateToken(anotherOwnerId.getValue());
//
//        UserId ownerId = UserId.of("ownerId");
//
//        RefreshTokenId refreshTokenId = refreshTokenRepository.nextRefreshTokenId();
//        LocalDateTime refreshTokenIssuedAt = LocalDateTime.now();
//        LocalDateTime refreshTokenExpirationTime = refreshTokenIssuedAt.plusDays(30);
//        RefreshToken refreshToken = new RefreshToken(
//                refreshTokenId,
//                "refreshtoken",
//                ownerId,
//                refreshTokenExpirationTime,
//                refreshTokenIssuedAt
//        );
//        refreshTokenRepository.save(refreshToken);
//
//        //expected
//        assertThatThrownBy(() -> sut.tokenRefresh(accessToken, refreshToken.getToken()))
//                .isInstanceOf(NotFoundRefreshTokenException.class);
//    }
//
//    @Test
//    @DisplayName("입력한 리프레시 토큰과 사용자가 최근에 발급한 리프레시 토큰이 일치하지 않으면 토큰을 재발급할 수 없다.")
//    void givenInputRefreshTokenIsNotEqualToUsersLatestRefreshToken_whenTokenRefresh_thenExceptionShouldBeThrown() {
//        //given
//        UserId ownerId = UserId.of("ownerId");
//
//        String accessToken = jwtTokenProvider.generateToken(ownerId.getValue());
//
//        List<RefreshToken> refreshTokens = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            RefreshTokenId refreshTokenId = refreshTokenRepository.nextRefreshTokenId();
//            LocalDateTime refreshTokenIssuedAt = LocalDateTime.now();
//            LocalDateTime refreshTokenExpirationTime = refreshTokenIssuedAt.plusDays(30);
//            RefreshToken refreshToken = new RefreshToken(
//                    refreshTokenId,
//                    "refreshtoken" + i,
//                    ownerId,
//                    refreshTokenExpirationTime,
//                    refreshTokenIssuedAt
//            );
//            refreshTokenRepository.save(refreshToken);
//            refreshTokens.add(refreshToken);
//        }
//
//
//        //expected
//        String firstRefreshToken = refreshTokens.get(0).getToken();
//        assertThatThrownBy(() -> sut.tokenRefresh(accessToken, firstRefreshToken))
//                .isInstanceOf(InvalidRefreshTokenException.class);
//    }
//
//    @Test
//    @DisplayName("만료된 리프레시 토큰으로 토큰을 재발급할 수 없다.")
//    void givenExpiredRefreshToken_whenTokenRefresh_thenExceptionShouldBeThrown() {
//        //given
//        UserId ownerId = UserId.of("ownerId");
//
//        String accessToken = jwtTokenProvider.generateToken(ownerId.getValue());
//
//        RefreshTokenId refreshTokenId = refreshTokenRepository.nextRefreshTokenId();
//        LocalDateTime refreshTokenIssuedAt = LocalDateTime.now().minusMonths(2);
//        LocalDateTime refreshTokenExpirationTime = refreshTokenIssuedAt.plusDays(30);
//        RefreshToken refreshToken = new RefreshToken(
//                refreshTokenId,
//                "refreshtoken",
//                ownerId,
//                refreshTokenExpirationTime,
//                refreshTokenIssuedAt
//        );
//        refreshTokenRepository.save(refreshToken);
//
//        //expected
//        assertThatThrownBy(() -> sut.tokenRefresh(accessToken, refreshToken.getToken()))
//                .isInstanceOf(ExpiredRefreshTokenException.class);
//
//    }
}