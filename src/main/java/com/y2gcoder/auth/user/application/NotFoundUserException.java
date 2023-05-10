package com.y2gcoder.auth.user.application;

import com.y2gcoder.auth.user.domain.UserId;

public class NotFoundUserException extends RuntimeException {

    public NotFoundUserException(UserId userId) {
        super(String.format("해당 유저를 찾을 수 없습니다. userId=%s", userId.getValue()));
    }
}
