package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.OwnerService;
import com.y2gcoder.auth.auth.infra.FakeAuthorizationCodeRepository;
import com.y2gcoder.auth.auth.infra.StubOwnerService;
import com.y2gcoder.auth.common.application.Time;
import com.y2gcoder.auth.common.infra.StubTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class IssueAuthorizationCodeServiceTest {
    private OwnerService ownerService;
    private AuthorizationCodeRepository authorizationCodeRepository;
    private Time time;
    private Long expirationMinutes;

    private IssueAuthorizationCodeService sut;

    @BeforeEach
    void setUp() {
        ownerService = new StubOwnerService();
        authorizationCodeRepository = new FakeAuthorizationCodeRepository();
        time = new StubTime(LocalDateTime.of(2023, 4, 24, 17, 48));
        expirationMinutes = 5L;
        sut = new IssueAuthorizationCodeService(
                ownerService,
                authorizationCodeRepository,
                time,
                expirationMinutes
        );
    }

    @Test
    @DisplayName("인증 코드를 발급할 수 있다.")
    void whenIssueAuthorizationCode_thenAuthorizationCodeIsReturned() {
        //given
        String email = "test@test.com";
        String password = "password";

        time = new StubTime(LocalDateTime.now());
        sut = new IssueAuthorizationCodeService(
                ownerService,
                authorizationCodeRepository,
                time,
                expirationMinutes
        );

        //when
        AuthorizationCode result = sut.issueAuthorizationCode(email, password);

        //then
        assertThat(result.isAvailable()).isTrue();
        assertThat(result.getExpirationTime()).isEqualToIgnoringNanos(time.now().plusMinutes(5));
    }

    @Test
    @DisplayName("만료된 인증 코드는 사용할 수 없다.")
    void givenExpiredAuthorizationCode_whenIssueAuthorizationCode_thenAuthorizationCodeIsUnavailable() {
        //given
        String email = "test@test.com";
        String password = "password";

        //when
        AuthorizationCode result = sut.issueAuthorizationCode(email, password);

        //then
        assertThat(result.isExpired()).isTrue();
        assertThat(result.isUsed()).isFalse();
        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("사용한 인증 코드는 재사용할 수 없다.")
    void givenUsedAuthorizationCode_whenIssueAuthorizationCode_thenAuthorizationCodeIsUnavailable() {
        //given
        String email = "test@test.com";
        String password = "password";

        time = new StubTime(LocalDateTime.now());
        sut = new IssueAuthorizationCodeService(
                ownerService,
                authorizationCodeRepository,
                time,
                expirationMinutes
        );

        //when
        AuthorizationCode result = sut.issueAuthorizationCode(email, password);
        result.markAsUsed();

        //then
        assertThat(result.isUsed()).isTrue();
        assertThat(result.isExpired()).isFalse();
        assertThat(result.isAvailable()).isFalse();
    }
}