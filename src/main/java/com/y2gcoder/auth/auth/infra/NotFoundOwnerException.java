package com.y2gcoder.auth.auth.infra;

public class NotFoundOwnerException extends RuntimeException {

    public NotFoundOwnerException() {
        super("해당하는 소유자를 찾지 못했습니다.");
    }
}
