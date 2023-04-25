package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.application.AuthorizationCodeRepository;
import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FakeAuthorizationCodeRepository implements AuthorizationCodeRepository {

    private final Map<AuthorizationCodeId, AuthorizationCode> fakeAuthorizationCodes = new HashMap<>();

    @Override
    public void save(AuthorizationCode authorizationCode) {
        fakeAuthorizationCodes.put(authorizationCode.getId(), authorizationCode);
    }

    @Override
    public AuthorizationCodeId nextAuthorizationCodeId() {
        return AuthorizationCodeId.of(UUID.randomUUID().toString());
    }
}
