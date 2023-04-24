package com.y2gcoder.auth.user.application;

import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;

    public void signUp(String email, String password, String name) {
        checkIfUserExistsByEmail(email);

        UserId userId = userRepository.nextUserId();
        User user = new User(userId, email, password, name);
        userRepository.save(user);
    }

    private void checkIfUserExistsByEmail(String email) {
        if (userRepository.existsByEmail(email)) throw new UserWithEmailExistsException();
    }
}
