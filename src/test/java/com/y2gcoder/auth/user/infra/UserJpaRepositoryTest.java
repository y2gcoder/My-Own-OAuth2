package com.y2gcoder.auth.user.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository sut;

    @DisplayName("해당 이메일을 가진 유저가 있는지 확인한다.")
    @Test
    void existsByEmail() {
        // given
        String targetEmail = "test@test.com";
        sut.save(new UserJpaEntity(
                "id",
                targetEmail,
                "password",
                "name",
                null
        ));

        // when
        boolean result = sut.existsByEmail(targetEmail);

        // then
        assertThat(result).isTrue();
    }
}