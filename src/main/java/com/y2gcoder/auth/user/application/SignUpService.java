package com.y2gcoder.auth.user.application;

import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;

    public User signUp(String email, String password, String name) {
        validatePassword(password);
        validateName(name);
        checkIfUserExistsByEmail(email);

        UserId userId = userRepository.nextUserId();
        return userRepository.save(new User(userId, email, password, name, null));
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
