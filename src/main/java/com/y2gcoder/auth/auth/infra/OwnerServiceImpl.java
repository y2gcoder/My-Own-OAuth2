package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.OwnerService;
import com.y2gcoder.auth.user.application.UserRepository;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserId getOwnerId(String email, String password) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(NotFoundOwnerException::new);
        verifyOwnerPassword(password, user.getPassword());
        return user.getId();
    }

    private void verifyOwnerPassword(String rawPassword, String encryptedPassword) {
        if (!passwordEncoder.matches(rawPassword, encryptedPassword)) {
            throw new InvalidPasswordException();
        }
    }
}
