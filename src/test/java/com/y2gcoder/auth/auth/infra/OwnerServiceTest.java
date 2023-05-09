package com.y2gcoder.auth.auth.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.y2gcoder.auth.auth.AuthIntegrationTestSupport;
import com.y2gcoder.auth.auth.domain.OwnerService;
import com.y2gcoder.auth.user.domain.UserId;
import com.y2gcoder.auth.user.infra.UserJpaEntity;
import com.y2gcoder.auth.user.infra.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

class OwnerServiceTest extends AuthIntegrationTestSupport {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OwnerService sut;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @DisplayName("이메일과 비밀번호로 소유자 ID를 가져올 수 있다.")
    @Test
    void getOwnerId() {
        // given
        String email = "test@test.com";
        String password = "test1234";
        String encryptedPassword = passwordEncoder.encode(password);
        userJpaRepository.save(new UserJpaEntity(
                "userId",
                email,
                encryptedPassword,
                "name",
                null
        ));

        // when
        UserId result = sut.getOwnerId(email, password);

        // then
        assertThat(result).isEqualTo(UserId.of("userId"));
    }

    @DisplayName("이메일로 소유자를 찾을 수 없다면 소유자 ID를 찾을 수 없다.")
    @Test
    void getOwnerIdWhenNotFoundOwnerByEmail() {
        // given
        String email = "test@test.com";
        String password = "test1234";

        // expected
        assertThatThrownBy(() -> sut.getOwnerId(email, password))
                .isInstanceOf(NotFoundOwnerException.class)
                .hasMessage("해당하는 소유자를 찾지 못했습니다.");
    }

    @DisplayName("잘못된 비밀번호로 소유자 ID를 찾을 수 없다.")
    @Test
    void getOwnerIdWithInvalidPassword() {
        // given
        String email = "test@test.com";
        String password = "test1234";
        String encryptedPassword = passwordEncoder.encode(password);
        userJpaRepository.save(new UserJpaEntity(
                "userId",
                email,
                encryptedPassword,
                "name",
                null
        ));

        // expected
        assertThatThrownBy(() -> sut.getOwnerId(email, "newtest1234"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");

    }


}