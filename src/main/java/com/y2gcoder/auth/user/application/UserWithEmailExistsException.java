package com.y2gcoder.auth.user.application;

import com.y2gcoder.auth.common.application.BusinessException;

public class UserWithEmailExistsException extends BusinessException {

    public UserWithEmailExistsException(String email) {
        super(String.format("해당 이메일을 가진 회원이 존재하지 않습니다. email=%s", email));
    }
}
