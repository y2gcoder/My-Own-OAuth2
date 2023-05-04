package com.y2gcoder.auth.auth.ui;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y2gcoder.auth.auth.application.IssueAuthorizationCodeService;
import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeStatus;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = IssueAuthorizationCodeController.class)
class IssueAuthorizationCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IssueAuthorizationCodeService issueAuthorizationCodeService;

    @DisplayName("이메일과 비밀번호로 인증 코드를 발급한다.")
    @Test
    void issueAuthorizationCode() throws Exception {
        // given
        String email = "test@test.com";
        String password = "password";
        IssueAuthorizationCodeRequest request = IssueAuthorizationCodeRequest.builder()
                .email(email)
                .password(password)
                .build();

        LocalDateTime expirationTime = LocalDateTime.of(2023, 5, 4, 9, 43);
        given(issueAuthorizationCodeService.issueAuthorizationCode(email, password))
                .willReturn(new AuthorizationCode(
                        AuthorizationCodeId.of("authorizationCodeId"),
                        "code",
                        expirationTime,
                        AuthorizationCodeStatus.ISSUED,
                        UserId.of("userId")
                ));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String formattedExpirationTime = expirationTime.format(formatter);

        // expected
        mockMvc.perform(post("/auth/code")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("code"))
                .andExpect(jsonPath("$.expiration_time").value(formattedExpirationTime));
    }

    @DisplayName("빈 이메일로 인증코드를 발급할 수 없다.")
    @Test
    void issueAuthorizationCodeWithBlankEmail() throws Exception {
        // given
        String password = "password";
        IssueAuthorizationCodeRequest request = IssueAuthorizationCodeRequest.builder()
                .email("")
                .password(password)
                .build();

        // expected
        mockMvc.perform(post("/auth/code")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("이메일은 필수값입니다."));
    }

    @DisplayName("올바르지 않은 형식의 이메일로 인증코드를 발급할 수 없다.")
    @Test
    void issueAuthorizationCodeWithNoEmailPattern() throws Exception {
        // given
        String email = "test";
        String password = "password";
        IssueAuthorizationCodeRequest request = IssueAuthorizationCodeRequest.builder()
                .email(email)
                .password(password)
                .build();

        // expected
        mockMvc.perform(post("/auth/code")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message")
                        .value("올바르지 않은 이메일 형식입니다."));
    }

    @DisplayName("빈 비밀번호로 인증코드를 발급할 수 없다.")
    @Test
    void issueAuthorizationCodeWithBlankPassword() throws Exception {
        // given
        String email = "test@test.com";
        IssueAuthorizationCodeRequest request = IssueAuthorizationCodeRequest.builder()
                .email(email)
                .password("")
                .build();

        // expected
        mockMvc.perform(post("/auth/code")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message")
                        .value("비밀번호는 필수값입니다."));

    }
}