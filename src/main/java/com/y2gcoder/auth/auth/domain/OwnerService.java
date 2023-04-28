package com.y2gcoder.auth.auth.domain;

import com.y2gcoder.auth.user.domain.UserId;

public interface OwnerService {
    UserId getOwnerId(String email, String password);
}
