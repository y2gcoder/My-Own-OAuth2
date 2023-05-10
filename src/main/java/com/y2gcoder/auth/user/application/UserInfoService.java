package com.y2gcoder.auth.user.application;

import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserInfoService {
    private final UserRepository userRepository;

    public User findById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException(userId));
    }
}
