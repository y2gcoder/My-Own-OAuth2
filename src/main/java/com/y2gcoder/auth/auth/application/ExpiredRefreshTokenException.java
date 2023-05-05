package com.y2gcoder.auth.auth.application;

public class ExpiredRefreshTokenException extends RuntimeException {

    public ExpiredRefreshTokenException() {
        super("리프레시 토큰이 만료되었습니다.");
    }
}
