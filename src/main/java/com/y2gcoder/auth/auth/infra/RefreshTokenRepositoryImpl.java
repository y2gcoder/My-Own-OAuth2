package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.application.RefreshTokenRepository;
import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.domain.RefreshTokenId;
import com.y2gcoder.auth.user.domain.UserId;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenJpaEntity refreshTokenJpaEntity = RefreshTokenJpaEntity.fromDomain(
                refreshToken);
        return refreshTokenJpaRepository.save(refreshTokenJpaEntity).toDomain();
    }

    @Override
    public RefreshTokenId nextRefreshTokenId() {
        return RefreshTokenId.of(UUID.randomUUID().toString());
    }

    @Override
    public Optional<RefreshToken> findLatestRefreshTokenByOwnerId(UserId ownerId) {
        return refreshTokenJpaRepository.findFirstByOwnerIdOrderByIssuedAtDesc(ownerId.getValue())
                .map(RefreshTokenJpaEntity::toDomain);
    }

}
