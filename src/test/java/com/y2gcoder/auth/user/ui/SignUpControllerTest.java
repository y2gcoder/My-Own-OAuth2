package com.y2gcoder.auth.user.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y2gcoder.auth.user.application.SignUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SignUpController.class)
class SignUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SignUpService signUpService;

    @DisplayName("회원가입한다.")
    @Test
    void signUp() throws Exception {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@test.com")
                .password("password")
                .name("name")
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
}