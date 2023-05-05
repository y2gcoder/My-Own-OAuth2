package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.AuthorizationCode;
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
public class SignInService {

    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProvider refreshTokenProvider;

    public SignInDto signIn(String code, LocalDateTime currentTime) {
        UserId ownerId = getOwnerIdBy(code, currentTime);

        String accessToken = jwtTokenProvider.generateToken(ownerId.getValue(), currentTime);

        RefreshToken refreshToken = createRefreshToken(currentTime, ownerId);

        return createSignInDto(accessToken, refreshToken);
    }

    private UserId getOwnerIdBy(String code, LocalDateTime currentTime) {
        AuthorizationCode authorizationCode = getAuthorizationCodeBy(code, currentTime);
        UserId ownerId = authorizationCode.getOwnerId();
        markAuthorizationCodeAsUsed(authorizationCode);
        return ownerId;
    }

    private AuthorizationCode getAuthorizationCodeBy(String code, LocalDateTime currentTime) {
        AuthorizationCode authorizationCode = authorizationCodeRepository.findByCode(code)
                .orElseThrow(NotFoundAuthorizationCodeException::new);
        if (!authorizationCode.isAvailable(currentTime)) {
            throw new UnavailableAuthorizationCodeException();
        }
        return authorizationCode;
    }

    private void markAuthorizationCodeAsUsed(AuthorizationCode authorizationCode) {
        authorizationCodeRepository.update(authorizationCode.getId(),
                AuthorizationCode::markAsUsed);
    }

    private RefreshToken createRefreshToken(LocalDateTime currentTime, UserId ownerId) {
        RefreshTokenId refreshTokenId = refreshTokenRepository.nextRefreshTokenId();
        LocalDateTime refreshTokenExpirationTime = refreshTokenProvider.getExpirationTime(
                currentTime);

        return refreshTokenRepository.save(new RefreshToken(refreshTokenId,
                refreshTokenProvider.generateToken(),
                ownerId,
                refreshTokenExpirationTime, currentTime));
    }

    private SignInDto createSignInDto(String accessToken, RefreshToken refreshToken) {
        LocalDateTime accessTokenExpirationTime = jwtTokenProvider.getExpiration(accessToken);

        AccessTokenDto accessTokenDto = new AccessTokenDto(accessToken, accessTokenExpirationTime);
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken.getToken(),
                refreshToken.getExpirationTime());

        return new SignInDto(accessTokenDto, refreshTokenDto);
    }
}
