package com.y2gcoder.auth.user.application;

import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import com.y2gcoder.auth.user.infra.FakeUserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignUpServiceTest {

    private UserRepository userRepository;
    private SignUpService sut;

    @BeforeEach
    void init() {
        userRepository = new FakeUserRepository();
        sut = new SignUpService(userRepository);
    }


    @Test
    @DisplayName("새로운 사용자를 만든다.")
    void whenCreateUser_thenUserIsCreated() {
        //given
        String email = "test@test.com";
        String password = "password";
        String name = "테스터";

        //when
        sut.signUp(email, password, name);

        //then
        Map<UserId, User> store = ((FakeUserRepository) userRepository).getStore();
        Assertions.assertThat(store.values().stream().anyMatch(u -> u.getEmail().equals(email)))
                .isTrue();
    }

    @Test
    @DisplayName("같은 이메일을 가진 사용자를 둘 이상 만들 수 없다.")
    void givenDuplicateEmail_whenCreateUser_thenExceptionShouldBeThrown() {
        //given
        String email = "test@test.com";
        String password = "password";
        String name = "테스터";
        UserId userId = userRepository.nextUserId();
        User user = new User(userId, email, password, name, null);
        userRepository.save(user);

        //expected
        String newPassword = "newpassword";
        String newName = "새로운 테스터";
        assertThatThrownBy(() -> sut.signUp(email, newPassword, name))
                .isInstanceOf(UserWithEmailExistsException.class);
    }

}