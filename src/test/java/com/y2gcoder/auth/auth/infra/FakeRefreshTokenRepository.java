package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.application.RefreshTokenRepository;
import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.domain.RefreshTokenId;
import com.y2gcoder.auth.user.domain.UserId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<RefreshTokenId, RefreshToken> fakeRefreshTokens = new HashMap<>();

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        fakeRefreshTokens.put(refreshToken.getId(), refreshToken);
        return refreshToken;
    }

    @Override
    public RefreshTokenId nextRefreshTokenId() {
        return RefreshTokenId.of(UUID.randomUUID().toString());
    }

    @Override
    public Optional<RefreshToken> findLatestRefreshTokenByOwnerId(UserId ownerId) {
        return fakeRefreshTokens.values().stream()
                .filter(refreshToken -> refreshToken.getOwnerId().equals(ownerId))
                .max(Comparator.comparing(RefreshToken::getIssuedAt));
    }

}
