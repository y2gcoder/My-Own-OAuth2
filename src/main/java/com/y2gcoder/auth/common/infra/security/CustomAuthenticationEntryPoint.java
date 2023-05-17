package com.y2gcoder.auth.common.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y2gcoder.auth.common.ui.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        log.warn("인증이 필요한 엔드포인트입니다.", authException);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message("Authentication required. Please provide a valid token.")
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
