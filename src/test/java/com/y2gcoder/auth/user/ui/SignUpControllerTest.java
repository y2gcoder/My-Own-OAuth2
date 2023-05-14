package com.y2gcoder.auth.user.ui;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.y2gcoder.auth.user.UserWebMvcTestSupport;
import com.y2gcoder.auth.user.application.UserWithEmailExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class SignUpControllerTest extends UserWebMvcTestSupport {

    @DisplayName("회원가입한다.")
    @Test
    void signUp() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@test.com")
                .password("password")
                .name("name")
                .profileImageUrl("profileImage")
                .build();

        // expected
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @DisplayName("회원가입할 때, 이메일은 필수값이다.")
    @Test
    void signUpWithoutEmail() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .password("password")
                .name("name")
                .profileImageUrl("profileImage")
                .build();

        // expected
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("이메일은 필수값입니다."));
    }

    @DisplayName("회원가입할 때, 이메일은 이메일 형식이어야 한다.")
    @Test
    void signUpWithoutEmailPattern() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test")
                .password("password")
                .name("name")
                .profileImageUrl("profileImage")
                .build();

        // expected
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("올바르지 않은 이메일 형식입니다."));
    }

    @DisplayName("회원가입할 때, 비밀번호는 필수값이다.")
    @Test
    void signUpWithoutPassword() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@test.com")
                .name("name")
                .profileImageUrl("profileImage")
                .build();

        // expected
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("비밀번호는 필수값입니다."));
    }

    @DisplayName("회원가입할 때, 이름은 필수값이다.")
    @Test
    void signUpWithoutName() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@test.com")
                .password("password")
                .profileImageUrl("profileImage")
                .build();

        // expected
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("이름은 필수값입니다."));
    }

    @DisplayName("이미 가입한 이메일로 회원가일 할 수 없다.")
    @Test
    void signUpWithDuplicatedEmail() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@test.com")
                .password("password")
                .name("name")
                .profileImageUrl("profileImage")
                .build();

        given(signUpService.signUp(anyString(), anyString(), anyString(), anyString()))
                .willThrow(new UserWithEmailExistsException("test@test.com"));


        // expected
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("409"))
                .andExpect(jsonPath("$.message")
                        .value(String.format("해당 이메일을 가진 회원이 이미 존재합니다. email=%s",
                                request.getEmail())));

    }
}