package com.y2gcoder.auth.auth.application;

import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;

public interface AuthorizationCodeRepository {
    void save(AuthorizationCode authorizationCode);

    AuthorizationCodeId nextAuthorizationCodeId();
}
