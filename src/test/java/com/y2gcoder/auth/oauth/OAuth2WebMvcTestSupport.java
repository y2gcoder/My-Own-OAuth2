package com.y2gcoder.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.common.infra.security.SecurityConfig;
import com.y2gcoder.auth.oauth.ui.OAuth2AuthController;
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
        OAuth2AuthController.class
})
@Import({
        SecurityConfig.class
})
public abstract class OAuth2WebMvcTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

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
