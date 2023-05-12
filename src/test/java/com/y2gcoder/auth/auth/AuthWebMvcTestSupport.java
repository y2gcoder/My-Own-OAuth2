package com.y2gcoder.auth.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y2gcoder.auth.auth.application.IssueAuthorizationCodeService;
import com.y2gcoder.auth.auth.application.SignInService;
import com.y2gcoder.auth.auth.application.TokenRefreshService;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.auth.ui.IssueAuthorizationCodeController;
import com.y2gcoder.auth.auth.ui.SignInController;
import com.y2gcoder.auth.auth.ui.TokenRefreshController;
import com.y2gcoder.auth.common.infra.security.SecurityConfig;
import com.y2gcoder.auth.oauth.infra.OAuth2Config;
import com.y2gcoder.auth.user.application.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        IssueAuthorizationCodeController.class,
        SignInController.class,
        TokenRefreshController.class
})
@Import({
        SecurityConfig.class,
        OAuth2Config.class
})
public abstract class AuthWebMvcTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected IssueAuthorizationCodeService issueAuthorizationCodeService;

    @MockBean
    protected SignInService signInService;

    @MockBean
    protected TokenRefreshService tokenRefreshService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

}
