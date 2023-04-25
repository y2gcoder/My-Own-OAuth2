package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.OwnerService;
import com.y2gcoder.auth.user.domain.UserId;

public class StubOwnerService implements OwnerService {
    @Override
    public UserId getOwnerId(String email, String password) {
        return UserId.of("ownerid");
    }
}
