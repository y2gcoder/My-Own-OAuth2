package com.y2gcoder.auth.auth.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y2gcoder.auth.auth.application.AccessTokenDto;
import com.y2gcoder.auth.auth.application.RefreshTokenDto;
import com.y2gcoder.auth.auth.application.TokenRefreshDto;
import com.y2gcoder.auth.auth.application.TokenRefreshService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TokenRefreshController.class)
class TokenRefreshControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenRefreshService tokenRefreshService;

    @DisplayName("액세스 토큰과 리프레시 토큰으로 액세스 토큰을 재발급한다.")
    @Test
    void tokenRefresh() throws Exception {
        // given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .accessToken("access")
                .refreshToken("refresh")
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

        given(tokenRefreshService.tokenRefresh(anyString(), anyString(), any(LocalDateTime.class)))
                .willReturn(new TokenRefreshDto(
                        new AccessTokenDto("newAccess", accessTokenExpirationTime),
                        new RefreshTokenDto("newRefresh", refreshTokenExpirationTime)
                ));
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        // expected
        mockMvc.perform(post("/auth/token/refresh")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access.token").value("newAccess"))
                .andExpect(jsonPath("$.access.expiration_time").value(
                        accessTokenExpirationTime.format(formatter)))
                .andExpect(jsonPath("$.refresh.token").value("newRefresh"))
                .andExpect(jsonPath("$.refresh.expiration_time").value(
                        refreshTokenExpirationTime.format(formatter)));

    }

    @DisplayName("액세스 토큰이 있어야 토큰을 재발급할 수 있다.")
    @Test
    void tokenRefreshWithBlankAccessToken() throws Exception {
        // given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .accessToken("")
                .refreshToken("refresh")
                .build();

        // expected
        mockMvc.perform(post("/auth/token/refresh")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message")
                        .value("액세스 토큰은 필수값입니다."));
    }

    @DisplayName("리프레시 토큰이 있어야 토큰을 재발급할 수 있다.")
    @Test
    void tokenRefreshWithBlankRefreshToken() throws Exception {
        // given
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .accessToken("access")
                .refreshToken("")
                .build();

        // expected
        mockMvc.perform(post("/auth/token/refresh")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message")
                        .value("리프레시 토큰은 필수값입니다."));
    }


}