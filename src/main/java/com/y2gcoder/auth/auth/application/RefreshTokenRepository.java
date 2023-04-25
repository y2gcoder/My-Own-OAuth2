package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.domain.RefreshTokenId;
import com.y2gcoder.auth.user.domain.UserId;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);

    Optional<RefreshToken> findById(RefreshTokenId id);

    Optional<RefreshToken> findByToken(String token);

    RefreshTokenId nextRefreshTokenId();

    Optional<RefreshToken> findLatestRefreshTokenByOwnerId(UserId ownerId);
}
