package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.application.AuthorizationCodeRepository;
import com.y2gcoder.auth.auth.domain.AuthorizationCode;
import com.y2gcoder.auth.auth.domain.AuthorizationCodeId;
import com.y2gcoder.auth.auth.domain.NotFoundAuthorizationCodeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class FakeAuthorizationCodeRepository implements AuthorizationCodeRepository {

    private final Map<AuthorizationCodeId, AuthorizationCode> fakeAuthorizationCodes = new HashMap<>();

    @Override
    public AuthorizationCode save(AuthorizationCode authorizationCode) {
        fakeAuthorizationCodes.put(authorizationCode.getId(), authorizationCode);
        return authorizationCode;
    }

    @Override
    public AuthorizationCodeId nextAuthorizationCodeId() {
        return AuthorizationCodeId.of(UUID.randomUUID().toString());
    }

    @Override
    public Optional<AuthorizationCode> findByCode(String code) {
        for (AuthorizationCode authorizationCode : fakeAuthorizationCodes.values()) {
            if (authorizationCode.getCode().equals(code)) {
                return Optional.of(authorizationCode);
            }
        }
        return Optional.empty();
    }

    @Override
    public void update(AuthorizationCodeId id, Consumer<AuthorizationCode> modifier) {
        AuthorizationCode authorizationCode = fakeAuthorizationCodes.get(id);
        if (authorizationCode == null) {
            throw new NotFoundAuthorizationCodeException();
        }
        modifier.accept(authorizationCode);
    }
}
