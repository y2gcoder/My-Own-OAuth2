package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;

import java.util.Optional;
import java.util.function.Consumer;

public interface AuthorizationCodeRepository {

    AuthorizationCode save(AuthorizationCode authorizationCode);

    AuthorizationCodeId nextAuthorizationCodeId();

    Optional<AuthorizationCode> findByCode(String code);

    void update(AuthorizationCodeId id, Consumer<AuthorizationCode> modifier);
}
