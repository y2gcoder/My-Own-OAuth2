package com.y2gcoder.auth.user.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.user.UserIntegrationTestSupport;
import com.y2gcoder.auth.user.application.UserRepository;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserRepositoryTest extends UserIntegrationTestSupport {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserRepository sut;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @DisplayName("새로운 유저 ID를 발급한다.")
    @Test
    void nextUserId() {
        // given
        Set<UserId> userIds = new HashSet<>();

        // when
        for (int i = 0; i < 5; i++) {
            userIds.add(sut.nextUserId());
        }

        // then
        assertThat(userIds).hasSize(5);

    }

    @DisplayName("유저를 저장한다.")
    @Test
    void save() {
        // given
        User user = new User(
                UserId.of("id"),
                "test@test.com",
                "password",
                "name",
                null
        );

        // when
        sut.save(user);

        // then
        Optional<UserJpaEntity> optionalEntity = userJpaRepository.findById("id");
        assertThat(optionalEntity).isPresent();
    }

    @DisplayName("이메일로 유저를 찾을 수 있다.")
    @Test
    void findByEmail() {
        // given
        String email = "test@test.com";
        userJpaRepository.save(new UserJpaEntity(
                "userId",
                email,
                "test1234",
                "name",
                null
        ));

        // when
        Optional<User> result = sut.findByEmail(email);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(UserId.of("userId"));
    }


}