package com.y2gcoder.auth.auth.application;

public class RefreshTokenMismatchException extends RuntimeException {

    public RefreshTokenMismatchException() {
        super("리프레시 토큰 불일치: 요청된 리프레시 토큰과 저장된 리프레시 토큰이 일치하지 않습니다.");
    }
}
