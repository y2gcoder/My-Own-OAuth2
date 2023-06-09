package com.y2gcoder.auth.common.ui;

import com.y2gcoder.auth.auth.application.ExpiredRefreshTokenException;
import com.y2gcoder.auth.auth.application.InvalidAccessTokenException;
import com.y2gcoder.auth.auth.application.NotFoundAuthorizationCodeException;
import com.y2gcoder.auth.auth.application.NotFoundRefreshTokenException;
import com.y2gcoder.auth.auth.application.RefreshTokenMismatchException;
import com.y2gcoder.auth.auth.application.UnavailableAuthorizationCodeException;
import com.y2gcoder.auth.auth.infra.InvalidPasswordException;
import com.y2gcoder.auth.auth.infra.NotFoundOwnerException;
import com.y2gcoder.auth.oauth.application.UnsupportedOAuth2ProviderException;
import com.y2gcoder.auth.user.application.NotFoundUserException;
import com.y2gcoder.auth.user.application.UserWithEmailExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 회원가입
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserWithEmailExistsException.class)
    public ErrorResponse userWithEmailExistsException(UserWithEmailExistsException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.CONFLICT.value()))
                .message(e.getMessage())
                .build();
    }

    /**
     * 인증 코드 발급
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundOwnerException.class)
    public ErrorResponse notFoundOwnerException(NotFoundOwnerException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidPasswordException.class)
    public ErrorResponse invalidPasswordException(InvalidPasswordException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(e.getMessage())
                .build();
    }

    /**
     * 토큰 발급
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotFoundAuthorizationCodeException.class)
    public ErrorResponse notFoundAuthorizationCodeException(NotFoundAuthorizationCodeException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnavailableAuthorizationCodeException.class)
    public ErrorResponse unavailableAuthorizationCodeException(
            UnavailableAuthorizationCodeException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(e.getMessage())
                .build();
    }

    /**
     * 토큰 재발급
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidAccessTokenException.class)
    public ErrorResponse invalidAccessTokenException(
            InvalidAccessTokenException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundRefreshTokenException.class)
    public ErrorResponse notFoundRefreshTokenException(NotFoundRefreshTokenException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(RefreshTokenMismatchException.class)
    public ErrorResponse refreshTokenMismatchException(
            RefreshTokenMismatchException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ErrorResponse expiredRefreshTokenException(
            ExpiredRefreshTokenException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(e.getMessage())
                .build();
    }

    /**
     * 내 정보 찾기
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundUserException.class)
    public ErrorResponse notFoundUserException(NotFoundUserException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .message(e.getMessage())
                .build();
    }

    /**
     * OAuth2 인증
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UnsupportedOAuth2ProviderException.class)
    public ErrorResponse unsupportedOAuth2ProviderException(UnsupportedOAuth2ProviderException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse authenticationException(
            AuthenticationException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .message(e.getMessage())
                .build();
    }


    /**
     * 공통
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse bindException(BindException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse illegalArgumentException(IllegalArgumentException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .message(e.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse exception(Exception e) {
        log.error("An unknown exception has occurred!!", e);
        return ErrorResponse.builder()
                .code(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .message(e.getMessage())
                .build();
    }

}
