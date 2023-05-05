package com.y2gcoder.auth.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.BDDMockito.given;

import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeStatus;
import com.y2gcoder.auth.auth.infra.AuthorizationCodeJpaEntity;
import com.y2gcoder.auth.auth.infra.AuthorizationCodeJpaRepository;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.auth.infra.RefreshTokenJpaEntity;
import com.y2gcoder.auth.auth.infra.RefreshTokenJpaRepository;
import com.y2gcoder.auth.auth.infra.TokenProperties;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class SignInServiceTest {

    @Autowired
    private AuthorizationCodeJpaRepository authorizationCodeJpaRepository;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Autowired
    private TokenProperties tokenProperties;

    @MockBean
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
        LocalDateTime currentTime = LocalDateTime
                .of(2023, 5, 5, 23, 36, 0);
        LocalDateTime expirationTime = currentTime.plusMonths(1);
        UserId ownerId = UserId.of("userId");
        authorizationCodeJpaRepository.save(new AuthorizationCodeJpaEntity(
                authorizationCodeId.getValue(),
                code,
                AuthorizationCodeStatus.ISSUED,
                expirationTime,
                ownerId.getValue()
        ));

        given(jwtTokenProvider.generateToken("userId", currentTime))
                .willReturn("access");
        Duration accessTokenExpiration = tokenProperties.getAccess().getExpiration();
        given(jwtTokenProvider.getExpiration("access"))
                .willReturn(currentTime.plus(accessTokenExpiration));


        // when
        SignInDto result = sut.signIn(code, currentTime);

        // then
        // JwtToken은 모킹했으니까 이정도면 검증하면 되지 않을까?
        assertThat(result).isNotNull();
        String accessToken = result.getAccess().getToken();
        assertThat(accessToken).isEqualTo("access");
        assertThat(result.getAccess().getExpirationTime()).isEqualTo(
                currentTime.plus(accessTokenExpiration));

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
        LocalDateTime currentTime = LocalDateTime.of(2023, 5, 5, 23, 22, 0);

        // expected
        assertThatThrownBy(() -> sut.signIn(code, currentTime))
                .isInstanceOf(NotFoundAuthorizationCodeException.class)
                .hasMessage("인증 코드를 찾을 수 없습니다.");
    }

    @DisplayName("만료된 인증코드로 토큰을 발급할 수 없다.")
    @Test
    void signInWithExpiredAuthorizationCode() {
        // given
        AuthorizationCodeId authorizationCodeId = AuthorizationCodeId.of("authorizationCodeId");
        String code = "code";
        LocalDateTime currentTime = LocalDateTime.of(2023, 5, 5, 23, 22, 0);
        LocalDateTime expirationTime = currentTime.minusSeconds(1);
        UserId ownerId = UserId.of("userId");
        authorizationCodeJpaRepository.save(new AuthorizationCodeJpaEntity(
                authorizationCodeId.getValue(),
                code,
                AuthorizationCodeStatus.ISSUED,
                expirationTime,
                ownerId.getValue()
        ));

        // expected
        assertThatThrownBy(() -> sut.signIn(code, currentTime))
                .isInstanceOf(UnavailableAuthorizationCodeException.class)
                .hasMessage("사용할 수 없는 인증코드입니다.");
    }

    @DisplayName("이미 사용한 인증코드로 토큰을 발급할 수 없다.")
    @Test
    void signInWithUsedAuthorizationCode() {
        // given
        AuthorizationCodeId authorizationCodeId = AuthorizationCodeId.of("authorizationCodeId");
        String code = "code";
        LocalDateTime currentTime = LocalDateTime
                .of(2023, 5, 5, 23, 36, 0);
        LocalDateTime expirationTime = currentTime.plusMinutes(1);
        UserId ownerId = UserId.of("userId");
        authorizationCodeJpaRepository.save(new AuthorizationCodeJpaEntity(
                authorizationCodeId.getValue(),
                code,
                AuthorizationCodeStatus.USED,
                expirationTime,
                ownerId.getValue()
        ));

        // expected
        assertThatThrownBy(() -> sut.signIn(code, currentTime))
                .isInstanceOf(UnavailableAuthorizationCodeException.class)
                .hasMessage("사용할 수 없는 인증코드입니다.");
    }

}