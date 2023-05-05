package com.y2gcoder.auth.auth.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.y2gcoder.auth.auth.AuthWebMvcTestSupport;
import com.y2gcoder.auth.auth.application.AccessTokenDto;
import com.y2gcoder.auth.auth.application.NotFoundAuthorizationCodeException;
import com.y2gcoder.auth.auth.application.RefreshTokenDto;
import com.y2gcoder.auth.auth.application.SignInDto;
import com.y2gcoder.auth.auth.application.UnavailableAuthorizationCodeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
class SignInControllerTest extends AuthWebMvcTestSupport {

    @DisplayName("인증 코드로 액세스 토큰과 리프레시 토큰을 발급한다.")
    @Test
    void signIn() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
                .code("code")
                .build();

        LocalDateTime accessTokenExpirationTime = LocalDateTime.of(2023,
                5,
                4,
                14,
                2);
        LocalDateTime refreshTokenExpirationTime = LocalDateTime.of(2023,
                6,
                4,
                13,
                47);

        given(signInService.signIn(anyString(), any(LocalDateTime.class)))
                .willReturn(new SignInDto(
                        new AccessTokenDto("access", accessTokenExpirationTime),
                        new RefreshTokenDto("refresh", refreshTokenExpirationTime))
                );
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        // expected
        mockMvc.perform(post("/auth/token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access.token").value("access"))
                .andExpect(jsonPath("$.access.expiration_time").value(
                        accessTokenExpirationTime.format(formatter)))
                .andExpect(jsonPath("$.refresh.token").value("refresh"))
                .andExpect(jsonPath("$.refresh.expiration_time").value(
                        refreshTokenExpirationTime.format(formatter)));

    }

    @DisplayName("인증 코드가 있어야 토큰을 발급할 수 있다.")
    @Test
    void signInWithNoAuthorizationCode() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
                .build();

        // expected
        mockMvc.perform(post("/auth/token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message")
                        .value("인증 코드는 필수값입니다."));
    }

    @DisplayName("인증코드가 저장되어있지 않으면 토큰을 발급할 수 없다.")
    @Test
    void signInWithNotFoundAuthorizationCode() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
                .code("code")
                .build();

        given(signInService.signIn(anyString(), any(LocalDateTime.class)))
                .willThrow(new NotFoundAuthorizationCodeException());

        // expected
        mockMvc.perform(post("/auth/token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message")
                        .value("인증 코드를 찾을 수 없습니다."));
    }

    @DisplayName("인증코드를 사용할 수 없다면 토큰을 발급할 수 없다.")
    @Test
    void signInWithUnavailableAuthorizationCode() throws Exception {
        // given
        SignInRequest request = SignInRequest.builder()
                .code("code")
                .build();

        given(signInService.signIn(anyString(), any(LocalDateTime.class)))
                .willThrow(new UnavailableAuthorizationCodeException());

        // expected
        mockMvc.perform(post("/auth/token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message")
                        .value("사용할 수 없는 인증코드입니다."));
    }
}