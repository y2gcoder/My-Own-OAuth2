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
        //인증 코드로 소유자 id를 가져온다.(유효한 인증 코드인지 확인 필요)
        AuthorizationCode authorizationCode = getAuthorizationCodeBy(code);
        UserId ownerId = authorizationCode.getOwnerId();

        //소유자 id를 사용해서 access token을 만든다.
        String accessToken = jwtTokenProvider.generateToken(ownerId.getValue());
        LocalDateTime accessTokenExpirationTime = jwtTokenProvider.getExpiration(accessToken);

        //소유자 id를 사용해서 refresh token을 만든다.
        RefreshToken refreshToken = createRefreshToken(currentTime, ownerId);

        //Authorization Code를 사용 상태로 변경한다.
        authorizationCodeRepository.update(authorizationCode.getId(),
                AuthorizationCode::markAsUsed);

        //반환한다.
        AccessTokenDto accessTokenDto = new AccessTokenDto(accessToken, accessTokenExpirationTime);
        RefreshTokenDto refreshTokenDto = new RefreshTokenDto(refreshToken.getToken(),
                refreshToken.getExpirationTime());
        return new SignInDto(accessTokenDto, refreshTokenDto);
    }

    private AuthorizationCode getAuthorizationCodeBy(String code) {
        AuthorizationCode authorizationCode = authorizationCodeRepository.findByCode(code)
                .orElseThrow(NotFoundAuthorizationCodeException::new);
        if (!authorizationCode.isAvailable()) {
            throw new UnavailableAuthorizationCodeException();
        }
        return authorizationCode;
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
}
