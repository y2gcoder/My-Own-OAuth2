package com.y2gcoder.auth.auth.application;

public class UnavailableAuthorizationCodeException extends RuntimeException {

    public UnavailableAuthorizationCodeException() {
        super("사용할 수 없는 인증코드입니다.");
    }
}
