package com.y2gcoder.auth.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {


    @DisplayName("회원이 삭제된 상태인지 확인한다.")
    @Test
    void isDeleted() {
        // given
        LocalDateTime deletedAt = LocalDateTime.of(2023, 5, 3, 15, 28);
        User user = new User(UserId.of("id"),
                "test@test.com",
                "password",
                "name",
                deletedAt, "profileImage");

        // when
        boolean result = user.isDeleted();

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("회원을 삭제한다.")
    @Test
    void delete() {
        // given
        User user = new User(UserId.of("id"),
                "test@test.com",
                "password",
                "name",
                null, "profileImage");
        assertThat(user.isDeleted()).isFalse();

        LocalDateTime deletedAt = LocalDateTime.of(2023, 5, 3, 15, 28);

        // when
        user.delete(deletedAt);

        // then
        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getDeletedAt()).isEqualTo(deletedAt);
    }

    @DisplayName("이미 삭제된 회원을 다시 삭제할 수 없다.")
    @Test
    void deleteWhenDeletedUser() {
        // given
        LocalDateTime deletedAt = LocalDateTime.of(2023, 5, 3, 15, 28);
        User user = new User(UserId.of("id"),
                "test@test.com",
                "password",
                "name",
                deletedAt, "profileImage");

        assertThat(user.isDeleted()).isTrue();

        LocalDateTime newDeletedAt = LocalDateTime.of(2023, 5, 3, 15, 35);

        // expected
        assertThatThrownBy(() -> user.delete(newDeletedAt))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 삭제된 회원입니다.");
    }


}