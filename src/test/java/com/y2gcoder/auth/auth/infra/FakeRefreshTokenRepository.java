package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.application.RefreshTokenRepository;
import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.domain.RefreshTokenId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FakeRefreshTokenRepository implements RefreshTokenRepository {
    private final Map<RefreshTokenId, RefreshToken> fakeRefreshTokens = new HashMap<>();

    @Override
    public void save(RefreshToken refreshToken) {
        fakeRefreshTokens.put(refreshToken.getId(), refreshToken);
    }

    @Override
    public Optional<RefreshToken> findById(RefreshTokenId id) {
        return Optional.ofNullable(fakeRefreshTokens.get(id));
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        for (RefreshToken refreshToken : fakeRefreshTokens.values()) {
            if (refreshToken.getToken().equals(token)) {
                return Optional.of(refreshToken);
            }
        }
        return Optional.empty();
    }

    @Override
    public RefreshTokenId nextRefreshTokenId() {
        return RefreshTokenId.of(UUID.randomUUID().toString());
    }
}
