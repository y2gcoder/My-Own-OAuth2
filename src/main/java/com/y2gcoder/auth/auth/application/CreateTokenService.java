package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.domain.RefreshTokenId;
import com.y2gcoder.auth.auth.domain.RefreshTokenProvider;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProvider refreshTokenProvider;

    public AccessTokenDto createAccessToken(UserId userId, LocalDateTime currentTime) {
        String token = jwtTokenProvider.generateToken(userId.getValue(), currentTime);
        LocalDateTime expirationTime = jwtTokenProvider.getExpiration(token);
        return new AccessTokenDto(token, expirationTime);
    }

    public RefreshTokenDto createRefreshToken(UserId userId, LocalDateTime currentTime) {
        RefreshTokenId id = refreshTokenRepository.nextRefreshTokenId();
        String token = refreshTokenProvider.generateToken();
        LocalDateTime expirationTime = refreshTokenProvider.getExpirationTime(
                currentTime);

        RefreshToken refreshToken = refreshTokenRepository.save(new RefreshToken(
                id,
                token,
                userId,
                expirationTime,
                currentTime
        ));

        return new RefreshTokenDto(refreshToken.getToken(), refreshToken.getExpirationTime());
    }
}
