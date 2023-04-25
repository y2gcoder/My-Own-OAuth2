package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.application.RefreshTokenRepository;
import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.domain.RefreshTokenId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public void save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity refreshTokenJpaEntity = RefreshTokenJpaEntity.fromDomain(refreshToken);
        refreshTokenJpaRepository.save(refreshTokenJpaEntity);
    }

    @Override
    public Optional<RefreshToken> findById(RefreshTokenId id) {
        return Optional.empty();
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.empty();
    }

    @Override
    public RefreshTokenId nextRefreshTokenId() {
        return RefreshTokenId.of(UUID.randomUUID().toString());
    }
}
