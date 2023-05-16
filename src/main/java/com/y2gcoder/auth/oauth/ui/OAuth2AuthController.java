package com.y2gcoder.auth.oauth.ui;

import com.y2gcoder.auth.auth.application.AccessTokenDto;
import com.y2gcoder.auth.auth.application.CreateTokenService;
import com.y2gcoder.auth.auth.application.RefreshTokenDto;
import com.y2gcoder.auth.oauth.application.UnsupportedOAuth2ProviderException;
import com.y2gcoder.auth.oauth.application.dto.CustomOAuth2UserDetails;
import com.y2gcoder.auth.user.domain.UserId;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OAuth2AuthController {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final CreateTokenService createTokenService;

    @GetMapping("/oauth2/auth/{registrationId}")
    public ResponseEntity<?> getAuthorizationRequestUri(
            @PathVariable String registrationId,
            @RequestParam("redirect_uri") String redirectUri) {
        log.info("registrationId: {}", registrationId);
        log.info("redirect_uri: {}", redirectUri);

        ClientRegistration clientRegistration = getClientRegistration(
                registrationId);

        String authorizationRequestUri = getAuthorizationRequestUri(redirectUri,
                clientRegistration);

        Map<String, String> response = new HashMap<>();
        response.put("authorization_request_uri", authorizationRequestUri);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/oauth2/token/{registrationId}")
    public ResponseEntity<OAuth2SignInResponse> handleAuthCodeExchange(
            @PathVariable("registrationId") String registrationId,
            @RequestBody AuthCodeExchangeRequest authCodeExchangeRequest
    ) {
        // 1. 인증 코드로 액세스 토큰 교환
        ClientRegistration clientRegistration = getClientRegistration(registrationId);
        OAuth2AccessToken accessToken = createOAuth2AccessToken(
                authCodeExchangeRequest, clientRegistration);

        // 2.사용자 정보 조회 및 사용자 정보를 내 서비스에서 사용할 수 있도록 처리
        CustomOAuth2UserDetails userDetails = getUserDetails(
                clientRegistration, accessToken);

        // 3. JWT 토큰 생성 및 반환
        UserId userId = userDetails.getId();
        LocalDateTime now = LocalDateTime.now();
        AccessTokenDto accessTokenDto = createTokenService.createAccessToken(userId, now);
        RefreshTokenDto refreshTokenDto = createTokenService.createRefreshToken(userId, now);

        return ResponseEntity.ok(new OAuth2SignInResponse(accessTokenDto, refreshTokenDto));
    }

    private ClientRegistration getClientRegistration(String registrationId) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(
                registrationId);

        if (clientRegistration == null) {
            throw new UnsupportedOAuth2ProviderException(registrationId);
        }
        return clientRegistration;
    }

    private static String getAuthorizationRequestUri(String redirectUri,
            ClientRegistration clientRegistration) {
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .clientId(clientRegistration.getClientId())
                .scopes(clientRegistration.getScopes())
                .redirectUri(redirectUri)
                .build();

        return authorizationRequest.getAuthorizationRequestUri();
    }

    private OAuth2AccessToken createOAuth2AccessToken(
            AuthCodeExchangeRequest authCodeExchangeRequest,
            ClientRegistration clientRegistration) {
        String authorizationCode = URLDecoder.decode(authCodeExchangeRequest.getCode(),
                StandardCharsets.UTF_8);
        OAuth2AuthorizationCodeGrantRequest grantRequest = new OAuth2AuthorizationCodeGrantRequest(
                clientRegistration,
                new OAuth2AuthorizationExchange(
                        OAuth2AuthorizationRequest.authorizationCode()
                                .authorizationUri(clientRegistration.getProviderDetails()
                                        .getAuthorizationUri())
                                .clientId(clientRegistration.getClientId())
                                .scopes(clientRegistration.getScopes())
                                .redirectUri(authCodeExchangeRequest.getRedirectUri())
                                .build(),
                        OAuth2AuthorizationResponse.success(authorizationCode)
                                .redirectUri(
                                        authCodeExchangeRequest.getRedirectUri()).build()
                )
        );

        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient =
                new DefaultAuthorizationCodeTokenResponseClient();
        OAuth2AccessTokenResponse accessTokenResponse = accessTokenResponseClient.getTokenResponse(
                grantRequest);
        return accessTokenResponse.getAccessToken();
    }

    private CustomOAuth2UserDetails getUserDetails(
            ClientRegistration clientRegistration, OAuth2AccessToken accessToken) {
        OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(clientRegistration,
                accessToken);
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);

        if (!(oAuth2User instanceof CustomOAuth2UserDetails)) {
            throw new OAuth2AuthenticationException("Invalid user details");
        }

        return (CustomOAuth2UserDetails) oAuth2User;
    }
}
