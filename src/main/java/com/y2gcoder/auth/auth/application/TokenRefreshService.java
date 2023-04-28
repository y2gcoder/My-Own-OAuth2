package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.ExpiredRefreshTokenException;
import com.y2gcoder.auth.auth.domain.InvalidRefreshTokenException;
import com.y2gcoder.auth.auth.domain.NotFoundRefreshTokenException;
import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenRefreshDto tokenRefresh(String expiredAccessToken, String inputRefreshToken) {
        // 만료된 access token 에서 username 조회
        String username = jwtTokenProvider.getUsernameFromToken(expiredAccessToken);
        UserId ownerId = UserId.of(username);

        // 입력받은 refresh token 과 username 으로 조회한 refresh token 비교
        RefreshToken refreshToken = refreshTokenRepository
                .findLatestRefreshTokenByOwnerId(ownerId).orElseThrow(NotFoundRefreshTokenException::new);
        validateInputRefreshToken(inputRefreshToken, refreshToken);

        // 조회한 refresh token 이 만료되었는지 확인
        if (refreshToken.isExpired()) {
            throw new ExpiredRefreshTokenException();
        }

        // access token 을 재발급해 refresh token과 함께 보내주기
        String accessToken = jwtTokenProvider.generateToken(ownerId.getValue());
        LocalDateTime accessTokenExpirationTime = jwtTokenProvider.getExpiration(accessToken);

        AccessTokenDto accessTokenDto = new AccessTokenDto(accessToken, accessTokenExpirationTime);
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken.getToken(), refreshToken.getExpirationTime());

        return new TokenRefreshDto(accessTokenDto, refreshTokenDto);
    }

    private void validateInputRefreshToken(String inputRefreshToken, RefreshToken refreshToken) {
        if (!refreshToken.getToken().equals(inputRefreshToken)) {
            throw new InvalidRefreshTokenException();
        }
    }
}
