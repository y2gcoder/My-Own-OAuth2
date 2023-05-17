package com.y2gcoder.auth.oauth.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.y2gcoder.auth.oauth.OAuth2WebMvcTestSupport;
import com.y2gcoder.auth.oauth.domain.OAuth2Provider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

class OAuth2AuthControllerTest extends OAuth2WebMvcTestSupport {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @DisplayName("OAuth2 인증 요청용 URI 를 만들어 제공할 수 있다.(구글)")
    @Test
    void getAuthorizationRequestUri() throws Exception {
        // given
        OAuth2Provider provider = OAuth2Provider.GOOGLE;
        String registrationId = provider.getRegistrationId();

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(
                registrationId);
        String redirectUri = "http://localhost:3000/test";

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .clientId(clientRegistration.getClientId())
                .scopes(clientRegistration.getScopes())
                .redirectUri(redirectUri)
                .build();

        String authorizationRequestUri = authorizationRequest.getAuthorizationRequestUri();

        // expected
        mockMvc.perform(
                        get("/oauth2/code/{registrationId}", registrationId)
                                .queryParam("redirect_uri", redirectUri)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorization_request_uri")
                        .value(authorizationRequestUri));

    }

    @DisplayName("OAuth2 인증 요청용 URI 를 만들어 제공할 수 있다.(카카오)")
    @Test
    void getAuthorizationRequestUriWithKakao() throws Exception {
        // given
        OAuth2Provider provider = OAuth2Provider.KAKAO;
        String registrationId = provider.getRegistrationId();

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(
                registrationId);
        String redirectUri = "http://localhost:3000/test";

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .clientId(clientRegistration.getClientId())
                .scopes(clientRegistration.getScopes())
                .redirectUri(redirectUri)
                .build();

        String authorizationRequestUri = authorizationRequest.getAuthorizationRequestUri();

        // expected
        mockMvc.perform(
                        get("/oauth2/code/{registrationId}", registrationId)
                                .queryParam("redirect_uri", redirectUri)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorization_request_uri")
                        .value(authorizationRequestUri));

    }

    @DisplayName("잘못된 OAuth2 프로바이더로 OAuth2 인증 요청 URI를 생성할 수 없다.")
    @Test
    void getAuthorizationRequestUriWithWrongProvider() throws Exception {
        // given
        String registrationId = "wrong";
        String redirectUri = "http://localhost:3000/test";

        // expected
        mockMvc.perform(
                        get("/oauth2/code/{registrationId}", registrationId)
                                .queryParam("redirect_uri", redirectUri)
                ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(String.valueOf(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message")
                        .value(String.format(
                                "Unsupported OAuth2 provider with registrationId: '%s'",
                                registrationId)));

    }


}