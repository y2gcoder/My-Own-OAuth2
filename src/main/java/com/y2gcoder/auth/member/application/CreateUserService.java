package com.y2gcoder.auth.member.application;

import com.y2gcoder.auth.member.domain.User;
import com.y2gcoder.auth.member.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserService {
    private final UserRepository userRepository;

    public void create(String email, String password, String name) {
        checkIfUserExistsByEmail(email);

        UserId userId = userRepository.nextUserId();
        User user = new User(userId, email, password, name);
        userRepository.save(user);
    }

    private void checkIfUserExistsByEmail(String email) {
        if (userRepository.existsByEmail(email)) throw new UserWithEmailExistsException();
    }
}
