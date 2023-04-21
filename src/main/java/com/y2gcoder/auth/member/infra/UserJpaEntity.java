package com.y2gcoder.auth.member.infra;

import com.y2gcoder.auth.common.infra.BaseTimeEntity;
import com.y2gcoder.auth.member.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class UserJpaEntity extends BaseTimeEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private String name;

    private LocalDateTime deletedAt;

    public static UserJpaEntity fromDomain(User user) {
        return new UserJpaEntity(
                user.getId().getValue(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getDeletedAt()
        );
    }
}
