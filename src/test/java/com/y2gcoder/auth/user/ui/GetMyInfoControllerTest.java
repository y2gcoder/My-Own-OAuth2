package com.y2gcoder.auth.user.ui;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.y2gcoder.auth.user.UserWebMvcTestSupport;
import com.y2gcoder.auth.user.application.NotFoundUserException;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class GetMyInfoControllerTest extends UserWebMvcTestSupport {

    @DisplayName("발급한 토큰으로 내 정보를 찾을 수 있다.")
    @Test
    void getMyInfo() throws Exception {
        // given
        UserId userId = UserId.of("userId");
        User user = new User(
                userId,
                "test@test.com",
                "test1234",
                "name",
                null
        );

        String token = "token";
        given(jwtTokenProvider.validateToken(token)).willReturn(true);
        given(jwtTokenProvider.getUsernameFrom(token)).willReturn(userId.getValue());
        given(userInfoService.findById(userId))
                .willReturn(user);

        // expected
        mockMvc.perform(
                        get("/users/me")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        String.format("Bearer %s", token)
                                )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.getValue()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.is_deleted").value(false));
    }


    @DisplayName("인증 헤더에 토큰을 보내야 유저 정보를 찾을 수 있다.")
    @Test
    void getMyInfoWithNoAccessToken() throws Exception {
        // given
        UserId userId = UserId.of("userId");
        User user = new User(
                userId,
                "test@test.com",
                "test1234",
                "name",
                null
        );

        // when
        String token = "token";
        given(jwtTokenProvider.validateToken(token)).willReturn(true);
        given(jwtTokenProvider.getUsernameFrom(token)).willReturn(userId.getValue());
        given(userInfoService.findById(userId))
                .willReturn(user);

        // expected
        mockMvc.perform(
                        get("/users/me")
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code")
                        .value(String.valueOf(HttpStatus.UNAUTHORIZED.value())))
                .andExpect(jsonPath("$.message")
                        .value("Authentication required. Please provide a valid token."));

    }

    @DisplayName("잘못된 토큰으로 유저 정보를 찾을 수 없다.")
    @Test
    void getMyInfoWithInvalidAccessToken() throws Exception {
        // given
        UserId userId = UserId.of("userId");
        User user = new User(
                userId,
                "test@test.com",
                "test1234",
                "name",
                null
        );

        String token = "invalid";
        given(jwtTokenProvider.validateToken(token)).willReturn(false);

        // expected
        mockMvc.perform(
                        get("/users/me")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        String.format("Bearer %s", token)
                                )
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code")
                        .value(String.valueOf(HttpStatus.UNAUTHORIZED.value())))
                .andExpect(jsonPath("$.message")
                        .value("Authentication required. Please provide a valid token."));

    }

    @DisplayName("만료된 토큰으로 유저 정보를 찾을 수 없다.")
    @Test
    void getMyInfoWithExpiredAccessToken() throws Exception {
        // given
        UserId userId = UserId.of("userId");
        User user = new User(
                userId,
                "test@test.com",
                "test1234",
                "name",
                null
        );

        String token = "expired";
        given(jwtTokenProvider.validateToken(token)).willReturn(false);
        given(jwtTokenProvider.getUsernameFrom(token)).willReturn(userId.getValue());

        // expected
        mockMvc.perform(
                        get("/users/me")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        String.format("Bearer %s", token)
                                )
                )
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code")
                        .value(String.valueOf(HttpStatus.UNAUTHORIZED.value())))
                .andExpect(jsonPath("$.message")
                        .value("Authentication required. Please provide a valid token."));

    }

    @DisplayName("토큰에 저장된 정보로 유저를 찾을 수 없을 수도 있다.")
    @Test
    void getMyInfoWithNotFoundUser() throws Exception {
        // given
        UserId userId = UserId.of("userId");
        User user = new User(
                userId,
                "test@test.com",
                "test1234",
                "name",
                null
        );

        String token = "token";
        given(jwtTokenProvider.validateToken(token)).willReturn(true);
        given(jwtTokenProvider.getUsernameFrom(token)).willReturn(userId.getValue());
        given(userInfoService.findById(userId))
                .willThrow(new NotFoundUserException(userId));

        // expected
        mockMvc.perform(
                        get("/users/me")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        String.format("Bearer %s", token)
                                )
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code")
                        .value(String.valueOf(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message")
                        .value(String.format("해당 유저를 찾을 수 없습니다. userId=%s", userId.getValue())));

    }

}