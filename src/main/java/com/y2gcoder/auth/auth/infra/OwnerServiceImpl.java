package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.NotFoundOwnerException;
import com.y2gcoder.auth.auth.domain.OwnerService;
import com.y2gcoder.auth.user.application.UserRepository;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {
    private final UserRepository userRepository;

    @Override
    public UserId getOwnerId(String email, String password) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundOwnerException("Not Found Owner"));
        verifyOwnerPassword(password, user.getPassword());
        return user.getId();
    }

    private void verifyOwnerPassword(String rawPassword, String encryptedPassword) {
        // TODO 암호화된 비밀번호와 대조하는 로직으로 바꿔야 함.
        if (!rawPassword.equals(encryptedPassword)) {
            throw new NotFoundOwnerException("Not Found Owner");
        }
    }
}
