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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        IssueAuthorizationCodeController.class,
        SignInController.class,
        TokenRefreshController.class
})
@Import({
        SecurityConfig.class
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
    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    @MockBean
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    @MockBean
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockBean
    private AuthenticationFailureHandler authenticationFailureHandler;

}
