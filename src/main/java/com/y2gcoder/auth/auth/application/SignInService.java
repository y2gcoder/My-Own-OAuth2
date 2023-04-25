package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.*;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class SignInService {
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProvider refreshTokenProvider;

    public SignInResponse signIn(String code) {
        //인증 코드로 소유자 id를 가져온다.(유효한 인증 코드인지 확인 필요)
        AuthorizationCode authorizationCode = authorizationCodeRepository.findByCode(code)
                .orElseThrow(NotFoundAuthorizationCodeException::new);
        if (!authorizationCode.isAvailable()) {
            throw new UnavailableAuthorizationCodeException();
        }
        UserId ownerId = authorizationCode.getOwnerId();

        //소유자 id를 사용해서 access token을 만든다.
        String accessToken = jwtTokenProvider.generateToken(ownerId.getValue());
        LocalDateTime accessTokenExpirationTime = jwtTokenProvider.getExpiration(accessToken);

        //소유자 id를 사용해서 refresh token을 만든다.
        RefreshTokenId refreshTokenId = refreshTokenRepository.nextRefreshTokenId();
        Duration refreshTokenExpiration = refreshTokenProvider.getExpiration();
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime refreshTokenExpirationTime = issuedAt.plus(refreshTokenExpiration);
        RefreshToken refreshToken = new RefreshToken(refreshTokenId,
                refreshTokenProvider.generateToken(),
                ownerId,
                refreshTokenExpirationTime, issuedAt);
        refreshTokenRepository.save(refreshToken);

        //Authorization Code를 사용 상태로 변경한다.
        authorizationCodeRepository.update(authorizationCode.getId(), AuthorizationCode::markAsUsed);

        //반환한다.
        AccessTokenDto accessTokenDto = new AccessTokenDto(accessToken, accessTokenExpirationTime);
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken.getToken(), refreshTokenExpirationTime);
        return new SignInResponse(accessTokenDto, refreshTokenDto);
    }
}
