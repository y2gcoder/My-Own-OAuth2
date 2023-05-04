package com.y2gcoder.auth.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import com.y2gcoder.auth.user.infra.UserJpaEntity;
import com.y2gcoder.auth.user.infra.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SignUpServiceTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SignUpService sut;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @DisplayName("이메일, 비밀번호, 이름을 받아 유저를 생성한다.")
    @Test
    void signUp() {
        // given
        String email = "test@test.com";
        String password = "test1234";
        String name = "name";

        // when
        User result = sut.signUp(email, password, name);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result).isNotNull()
                .extracting("email", "password", "name", "deletedAt")
                .contains(email, password, name, null);
    }

    @DisplayName("유저를 생성할 때 비밀번호는 8자리 이상이어야 한다.")
    @Test
    void signUpWithShortPassword() {
        // given
        String email = "test@test.com";
        String password = "test123";
        String name = "name";

        // expected
        assertThatThrownBy(() -> sut.signUp(email, password, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 8자리 이상이어야 합니다.");
    }

    @DisplayName("유저를 생성할 때 이름은 2글자 이상이어야 한다.")
    @Test
    void signUpWithShortName() {
        // given
        String email = "test@test.com";
        String password = "test1234";
        String name = "한";

        // expected
        assertThatThrownBy(() -> sut.signUp(email, password, name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름은 2글자 이상이어야 합니다.");
    }

    @DisplayName("이미 존재하는 유저의 이메일로 유저를 생성할 수 없다.")
    @Test
    void signUpWithEmailAlreadyExists() {
        // given
        UserId userId = userRepository.nextUserId();
        String email = "test@test.com";
        String password = "test1234";
        String name = "name";

        userJpaRepository.save(new UserJpaEntity(
                userId.getValue(),
                email,
                password,
                name,
                null
        ));

        // expected
        assertThatThrownBy(() -> sut.signUp(email, password, name))
                .isInstanceOf(UserWithEmailExistsException.class)
                .hasMessage(String.format("해당 이메일을 가진 회원이 이미 존재합니다. email=%s", email));

    }
}