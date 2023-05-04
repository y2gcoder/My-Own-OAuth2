package com.y2gcoder.auth.auth.application;

public class NotFoundAuthorizationCodeException extends RuntimeException {

    public NotFoundAuthorizationCodeException() {
        super("인증 코드를 찾을 수 없습니다.");
    }
}
