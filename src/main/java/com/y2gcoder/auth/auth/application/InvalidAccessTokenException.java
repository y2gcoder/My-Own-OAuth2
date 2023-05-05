package com.y2gcoder.auth.auth.application;

public class InvalidAccessTokenException extends RuntimeException {

    public InvalidAccessTokenException(Throwable cause) {
        super("유효하지 않은 액세스 토큰입니다.", cause);
    }
}
