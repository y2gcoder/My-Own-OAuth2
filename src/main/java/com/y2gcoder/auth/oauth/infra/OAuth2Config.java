package com.y2gcoder.auth.oauth.infra;

import com.y2gcoder.auth.auth.application.RefreshTokenRepository;
import com.y2gcoder.auth.auth.domain.RefreshTokenProvider;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.oauth.application.CustomOAuth2UserService;
import com.y2gcoder.auth.oauth.application.OAuth2AuthenticationRepository;
import com.y2gcoder.auth.user.application.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class OAuth2Config {

    private final OAuth2AuthenticationRepository oAuth2AuthenticationRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProvider refreshTokenProvider;

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new CustomOAuth2UserService(oAuth2AuthenticationRepository, userRepository);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new CookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                (CookieOAuth2AuthorizationRequestRepository) authorizationRequestRepository(),
                jwtTokenProvider,
                refreshTokenRepository,
                refreshTokenProvider);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler();
    }
}
