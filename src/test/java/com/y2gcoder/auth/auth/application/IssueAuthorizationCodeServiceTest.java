package com.y2gcoder.auth.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.infra.AuthorizationCodeJpaRepository;
import com.y2gcoder.auth.user.domain.UserId;
import com.y2gcoder.auth.user.infra.UserJpaEntity;
import com.y2gcoder.auth.user.infra.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IssueAuthorizationCodeServiceTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private AuthorizationCodeJpaRepository authorizationCodeJpaRepository;

    @Autowired
    private IssueAuthorizationCodeService sut;

    @BeforeEach
    void setUp() {
        userJpaRepository.save(new UserJpaEntity(
                "userId",
                "test@test.com",
                "test1234",
                "name",
                null
        ));
    }

    @AfterEach
    void tearDown() {
        authorizationCodeJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
    }

    @DisplayName("인증 코드를 발급할 수 있다.")
    @Test
    void issueAuthorizationCode() {
        // when
        AuthorizationCode result = sut
                .issueAuthorizationCode("test@test.com", "test1234");

        // then
        assertThat(result).isNotNull();
        assertThat(result.isAvailable()).isTrue();
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getOwnerId()).isEqualTo(UserId.of("userId"));
    }
}