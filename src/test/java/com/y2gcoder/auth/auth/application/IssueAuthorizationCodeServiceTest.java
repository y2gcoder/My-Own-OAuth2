package com.y2gcoder.auth.auth.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.y2gcoder.auth.auth.AuthIntegrationTestSupport;
import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeProvider;
import com.y2gcoder.auth.auth.infra.AuthorizationCodeJpaRepository;
import com.y2gcoder.auth.user.domain.UserId;
import com.y2gcoder.auth.user.infra.UserJpaEntity;
import com.y2gcoder.auth.user.infra.UserJpaRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

class IssueAuthorizationCodeServiceTest extends AuthIntegrationTestSupport {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private AuthorizationCodeJpaRepository authorizationCodeJpaRepository;

    @Autowired
    private AuthorizationCodeProvider authorizationCodeProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IssueAuthorizationCodeService sut;

    @BeforeEach
    void setUp() {
        userJpaRepository.save(new UserJpaEntity(
                "userId",
                "test@test.com",
                passwordEncoder.encode("test1234"),
                "name",
                null,
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
        LocalDateTime currentTime = LocalDateTime
                .of(2023, 5, 5, 23, 2, 0);
        AuthorizationCode result = sut
                .issueAuthorizationCode("test@test.com", "test1234", currentTime);

        // then
        LocalDateTime referenceTime = authorizationCodeProvider.getExpirationTime(currentTime)
                .minusNanos(1);
        assertThat(result).isNotNull();
        assertThat(result.isAvailable(referenceTime)).isTrue();
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getOwnerId()).isEqualTo(UserId.of("userId"));
    }
}