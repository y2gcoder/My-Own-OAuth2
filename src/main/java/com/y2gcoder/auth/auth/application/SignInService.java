package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.AuthorizationCode;
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
    private final CreateTokenService createTokenService;

    public SignInDto signIn(String code, LocalDateTime currentTime) {
        UserId ownerId = getOwnerIdBy(code, currentTime);

        AccessTokenDto accessTokenDto = createTokenService.createAccessToken(ownerId, currentTime);
        RefreshTokenDto refreshTokenDto = createTokenService.createRefreshToken(ownerId, currentTime);

        return new SignInDto(accessTokenDto, refreshTokenDto);
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

}
