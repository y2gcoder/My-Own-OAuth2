package com.y2gcoder.auth.auth.infra;


import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.domain.RefreshTokenId;
import com.y2gcoder.auth.common.infra.BaseTimeEntity;
import com.y2gcoder.auth.user.domain.UserId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity extends BaseTimeEntity {
    @Id
    private String id;
    private String token;
    private String ownerId;
    private LocalDateTime expirationTime;
    private LocalDateTime issuedAt;

    public static RefreshTokenJpaEntity fromDomain(RefreshToken refreshToken) {
        return new RefreshTokenJpaEntity(
                refreshToken.getId().getValue(),
                refreshToken.getToken(),
                refreshToken.getOwnerId().getValue(),
                refreshToken.getExpirationTime(),
                refreshToken.getIssuedAt()
        );
    }

    public RefreshToken toDomain() {
        return new RefreshToken(
                RefreshTokenId.of(id),
                token,
                UserId.of(ownerId),
                expirationTime,
                issuedAt
        );
    }
}
