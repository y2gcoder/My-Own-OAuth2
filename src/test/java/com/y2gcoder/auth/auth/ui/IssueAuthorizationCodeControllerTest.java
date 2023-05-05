package com.y2gcoder.auth.auth.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.y2gcoder.auth.auth.AuthWebMvcTestSupport;
import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeStatus;
import com.y2gcoder.auth.auth.infra.InvalidPasswordException;
import com.y2gcoder.auth.auth.infra.NotFoundOwnerException;
import com.y2gcoder.auth.user.domain.UserId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class IssueAuthorizationCodeControllerTest extends AuthWebMvcTestSupport {

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
        given(issueAuthorizationCodeService.issueAuthorizationCode(eq(email), eq(password),
                any(LocalDateTime.class)))
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

    @DisplayName("회원가입되지 않은 이메일로 인증코드를 발급할 수 없다.")
    @Test
    void issueAuthorizationCodeWithNotRegisteredEmail() throws Exception {
        // given
        String email = "test@test.com";
        String password = "password";
        IssueAuthorizationCodeRequest request = IssueAuthorizationCodeRequest.builder()
                .email(email)
                .password(password)
                .build();

        given(issueAuthorizationCodeService.issueAuthorizationCode(eq(email), eq(password),
                any(LocalDateTime.class)))
                .willThrow(new NotFoundOwnerException());

        // expected
        mockMvc.perform(post("/auth/code")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.message").value("해당하는 소유자를 찾지 못했습니다."));
    }

    @DisplayName("입력한 비밀번호와 저장된 비밀번호가 일치하지 않을 때, 인증코드를 발급할 수 없다.")
    @Test
    void issueAuthorizationCodeWhenMismatchPassword() throws Exception {
        // given
        String email = "test@test.com";
        String password = "password";
        IssueAuthorizationCodeRequest request = IssueAuthorizationCodeRequest.builder()
                .email(email)
                .password(password)
                .build();

        given(issueAuthorizationCodeService.issueAuthorizationCode(eq(email), eq(password),
                any(LocalDateTime.class)))
                .willThrow(new InvalidPasswordException());

        // expected
        mockMvc.perform(post("/auth/code")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }
}