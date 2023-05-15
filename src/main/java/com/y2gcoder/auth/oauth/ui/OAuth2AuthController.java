package com.y2gcoder.auth.oauth.ui;

import com.y2gcoder.auth.oauth.application.UnsupportedOAuth2ProviderException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuth2AuthController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @GetMapping("/oauth2/auth/{registrationId}")
    public ResponseEntity<?> getAuthorizationRequestUri(
            @PathVariable String registrationId,
            @RequestParam("redirect_uri") String redirectUri) {
        log.info("registrationId: {}", registrationId);
        log.info("redirect_uri: {}", redirectUri);

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(
                registrationId);

        if (clientRegistration == null) {
            throw new UnsupportedOAuth2ProviderException(registrationId);
        }

        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .clientId(clientRegistration.getClientId())
                .scopes(clientRegistration.getScopes())
                .redirectUri(redirectUri)
                .build();

        String authorizationRequestUri = authorizationRequest.getAuthorizationRequestUri();

        Map<String, String> response = new HashMap<>();
        response.put("authorization_request_uri", authorizationRequestUri);
        return ResponseEntity.ok(response);
    }
}
