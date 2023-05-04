package com.y2gcoder.auth.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeStatus;
import com.y2gcoder.auth.auth.infra.AuthorizationCodeJpaEntity;
import com.y2gcoder.auth.auth.infra.AuthorizationCodeJpaRepository;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.auth.infra.RefreshTokenJpaEntity;
import com.y2gcoder.auth.auth.infra.RefreshTokenJpaRepository;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SignInServiceTest {

    @Autowired
    private AuthorizationCodeJpaRepository authorizationCodeJpaRepository;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private SignInService sut;

    @AfterEach
    void tearDown() {
        refreshTokenJpaRepository.deleteAllInBatch();
        authorizationCodeJpaRepository.deleteAllInBatch();
    }

    @DisplayName("인증 코드로 토큰을 발급할 수 있다.")
    @Test
    void signIn() {
        // given
        AuthorizationCodeId authorizationCodeId = AuthorizationCodeId.of("authorizationCodeId");
        String code = "code";
        LocalDateTime expirationTime = LocalDateTime.now().plusMonths(1);
        UserId ownerId = UserId.of("userId");
        authorizationCodeJpaRepository.save(new AuthorizationCodeJpaEntity(
                authorizationCodeId.getValue(),
                code,
                AuthorizationCodeStatus.ISSUED,
                expirationTime,
                ownerId.getValue()
        ));

        // when
        SignInDto result = sut.signIn(code, LocalDateTime.now());

        // then
        assertThat(result).isNotNull();

        String accessToken = result.getAccess().getToken();
        jwtTokenProvider.validateToken(accessToken);
        String username = jwtTokenProvider.getUsernameFrom(accessToken);
        assertThat(username).isEqualTo(ownerId.getValue());
        assertThat(result.getAccess().getExpirationTime()).isEqualTo(
                jwtTokenProvider.getExpiration(accessToken));

        String refreshToken = result.getRefresh().getToken();
        LocalDateTime refreshTokenExpirationTime = result.getRefresh().getExpirationTime();
        List<RefreshTokenJpaEntity> refreshTokenJpaEntities = refreshTokenJpaRepository.findAll();
        assertThat(refreshTokenJpaEntities).hasSize(1)
                .extracting("token", "ownerId", "expirationTime")
                .containsExactlyInAnyOrder(
                        tuple(refreshToken, ownerId.getValue(), refreshTokenExpirationTime)
                );

        Optional<AuthorizationCodeJpaEntity> optionalAuthorizationCodeJpaEntity =
                authorizationCodeJpaRepository.findById(authorizationCodeId.getValue());
        assertThat(optionalAuthorizationCodeJpaEntity).isPresent();
        assertThat(optionalAuthorizationCodeJpaEntity.get().getStatus())
                .isEqualByComparingTo(AuthorizationCodeStatus.USED);
    }

    @DisplayName("인증 코드를 찾을 수 없으면 토큰을 발급할 수 없다.")
    @Test
    void signInWithNotFoundAuthorizationCode() {
        // given
        String code = "code";

        // expected
        assertThatThrownBy(() -> sut.signIn(code, LocalDateTime.now()))
                .isInstanceOf(NotFoundAuthorizationCodeException.class)
                .hasMessage("인증 코드를 찾을 수 없습니다.");
    }

    @DisplayName("만료된 인증코드로 토큰을 발급할 수 없다.")
    @Test
    void signInWithExpiredAuthorizationCode() {
        // given
        AuthorizationCodeId authorizationCodeId = AuthorizationCodeId.of("authorizationCodeId");
        String code = "code";
        LocalDateTime expirationTime = LocalDateTime.now().minusSeconds(1);
        UserId ownerId = UserId.of("userId");
        authorizationCodeJpaRepository.save(new AuthorizationCodeJpaEntity(
                authorizationCodeId.getValue(),
                code,
                AuthorizationCodeStatus.ISSUED,
                expirationTime,
                ownerId.getValue()
        ));

        // expected
        assertThatThrownBy(() -> sut.signIn(code, LocalDateTime.now()))
                .isInstanceOf(UnavailableAuthorizationCodeException.class)
                .hasMessage("사용할 수 없는 인증코드입니다.");
    }

    @DisplayName("이미 사용한 인증코드로 토큰을 발급할 수 없다.")
    @Test
    void signInWithUsedAuthorizationCode() {
        // given
        AuthorizationCodeId authorizationCodeId = AuthorizationCodeId.of("authorizationCodeId");
        String code = "code";
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(1);
        UserId ownerId = UserId.of("userId");
        authorizationCodeJpaRepository.save(new AuthorizationCodeJpaEntity(
                authorizationCodeId.getValue(),
                code,
                AuthorizationCodeStatus.USED,
                expirationTime,
                ownerId.getValue()
        ));

        // expected
        assertThatThrownBy(() -> sut.signIn(code, LocalDateTime.now()))
                .isInstanceOf(UnavailableAuthorizationCodeException.class)
                .hasMessage("사용할 수 없는 인증코드입니다.");
    }

}