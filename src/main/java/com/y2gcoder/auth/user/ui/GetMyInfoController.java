package com.y2gcoder.auth.user.ui;

import com.y2gcoder.auth.common.ui.LoggedInUserId;
import com.y2gcoder.auth.user.application.UserInfoService;
import com.y2gcoder.auth.user.domain.User;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetMyInfoController {
    private final UserInfoService userInfoService;

    @GetMapping("/users/me")
    public ResponseEntity<MyInfoResponse> getMyInfo(@LoggedInUserId UserId userId) {

        User user = userInfoService.findById(userId);
        return ResponseEntity.ok(new MyInfoResponse(user));
    }

}
