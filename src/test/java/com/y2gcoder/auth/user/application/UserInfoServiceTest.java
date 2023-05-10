package com.y2gcoder.auth.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.y2gcoder.auth.user.UserIntegrationTestSupport;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import com.y2gcoder.auth.user.infra.UserJpaEntity;
import com.y2gcoder.auth.user.infra.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserInfoServiceTest  extends UserIntegrationTestSupport {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoService sut;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @DisplayName("유저 ID로 해당 유저를 찾을 수 있다.")
    @Test
    void findById() {
        // given
        UserId userId = UserId.of("userId");
        UserJpaEntity jpaEntity = new UserJpaEntity(
                userId.getValue(),
                "test@test.com",
                "test1234",
                "name",
                null
        );
        userJpaRepository.save(jpaEntity);

        // when
        User result = sut.findById(userId);

        // then
        assertThat(result).extracting(
                "id", "email", "password", "name", "deletedAt"
        ).contains(
                UserId.of(jpaEntity.getId()),
                jpaEntity.getEmail(),
                jpaEntity.getPassword(),
                jpaEntity.getName(),
                jpaEntity.getDeletedAt()
        );
    }

    @DisplayName("해당 유저 ID를 가진 유저가 없을 수 있다.")
    @Test
    void findByIdWithNotFound() {
        // given
        UserId userId = UserId.of("userId");

        // expected
        assertThatThrownBy(() -> sut.findById(userId))
                .isInstanceOf(NotFoundUserException.class)
                .hasMessage(String.format("해당 유저를 찾을 수 없습니다. userId=%s", userId.getValue()));

    }
}