package com.y2gcoder.auth.user.application;

public class UserWithEmailExistsException extends RuntimeException {

    public UserWithEmailExistsException(String email) {
        super(String.format("해당 이메일을 가진 회원이 이미 존재합니다. email=%s", email));
    }
}
