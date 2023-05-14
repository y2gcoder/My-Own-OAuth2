package com.y2gcoder.auth.user.application;

import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signUp(String email, String password, String name, String profileImageUrl) {
        validatePassword(password);
        validateName(name);
        checkIfUserExistsByEmail(email);

        UserId userId = userRepository.nextUserId();
        String encryptedPassword = createEncryptedPassword(password);
        return userRepository.save(new User(userId,
                email,
                encryptedPassword,
                name,
                null,
                profileImageUrl));
    }

    private String createEncryptedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자리 이상이어야 합니다.");
        }
    }

    private void validateName(String name) {
        if (name.length() < 2) {
            throw new IllegalArgumentException("이름은 2글자 이상이어야 합니다.");
        }
    }

    private void checkIfUserExistsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserWithEmailExistsException(email);
        }
    }
}
