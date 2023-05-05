package com.y2gcoder.auth.auth.application;

public class NotFoundRefreshTokenException extends RuntimeException {

    public NotFoundRefreshTokenException() {
        super("리프레시 토큰을 찾을 수 없습니다.");
    }
}
