package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenRefreshDto tokenRefresh(String oldAccessToken, String inputRefreshToken,
            LocalDateTime currentTime) {
        UserId ownerId = getOwnerIdBy(oldAccessToken);

        RefreshToken refreshToken = getRefreshTokenBy(ownerId);
        validateRefreshToken(inputRefreshToken, refreshToken, currentTime);

        String accessToken = jwtTokenProvider.generateToken(ownerId.getValue(), currentTime);

        return createTokenRefreshDto(refreshToken, accessToken);
    }

    private UserId getOwnerIdBy(String oldAccessToken) {
        try {
            String username = jwtTokenProvider.getUsernameFrom(oldAccessToken);
            return UserId.of(username);
        } catch (Exception e) {
            throw new InvalidAccessTokenException(e);
        }

    }

    private RefreshToken getRefreshTokenBy(UserId ownerId) {
        return refreshTokenRepository
                .findLatestRefreshTokenByOwnerId(ownerId)
                .orElseThrow(NotFoundRefreshTokenException::new);
    }

    private void validateRefreshToken(String inputRefreshToken,
            RefreshToken refreshToken,
            LocalDateTime currentTime) {
        if (areRefreshTokensMatching(inputRefreshToken, refreshToken)) {
            throw new RefreshTokenMismatchException();
        }
        if (refreshToken.isExpiredAt(currentTime)) {
            throw new ExpiredRefreshTokenException();
        }
    }

    private static boolean areRefreshTokensMatching(String inputRefreshToken, RefreshToken refreshToken) {
        return !refreshToken.getToken().equals(inputRefreshToken);
    }

    private TokenRefreshDto createTokenRefreshDto(RefreshToken refreshToken, String accessToken) {
        LocalDateTime accessTokenExpirationTime = jwtTokenProvider.getExpiration(accessToken);

        AccessTokenDto accessTokenDto = new AccessTokenDto(accessToken, accessTokenExpirationTime);
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken.getToken(),
                refreshToken.getExpirationTime());

        return new TokenRefreshDto(accessTokenDto, refreshTokenDto);
    }
}
