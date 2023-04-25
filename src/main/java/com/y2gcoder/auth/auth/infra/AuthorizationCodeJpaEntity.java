package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeStatus;
import com.y2gcoder.auth.common.infra.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "authorization_codes")
public class AuthorizationCodeJpaEntity extends BaseTimeEntity {
    @Id
    private String id;


    private String code;

    @Enumerated(EnumType.STRING)
    private AuthorizationCodeStatus status;

    private LocalDateTime expirationTime;

    private String ownerId;

    public static AuthorizationCodeJpaEntity fromDomain(AuthorizationCode authorizationCode) {
        return new AuthorizationCodeJpaEntity(
                authorizationCode.getId().getValue(),
                authorizationCode.getCode(),
                authorizationCode.getStatus(),
                authorizationCode.getExpirationTime(),
                authorizationCode.getOwnerId().getValue()
        );
    }
}
