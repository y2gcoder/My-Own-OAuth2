package com.y2gcoder.auth.oauth.infra;

import com.y2gcoder.auth.auth.application.AccessTokenDto;
import com.y2gcoder.auth.auth.application.CreateTokenService;
import com.y2gcoder.auth.auth.application.RefreshTokenDto;
import com.y2gcoder.auth.oauth.application.dto.CustomOAuth2UserDetails;
import com.y2gcoder.auth.user.domain.UserId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    private final CreateTokenService createTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            log.info("Response has already bean committed!!!");
            return;
        }

        String redirectUri = determineTargetUrl(request, response, authentication);
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        String redirectUri = CookieUtils.getCookie(request,
                        CookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElseThrow(NotFoundRedirectUriException::new);

        CustomOAuth2UserDetails userDetails = (CustomOAuth2UserDetails) authentication.getPrincipal();
        UserId userId = userDetails.getId();
        LocalDateTime now = LocalDateTime.now();

        AccessTokenDto accessTokenDto = createTokenService.createAccessToken(userId, now);
        RefreshTokenDto refreshTokenDto = createTokenService.createRefreshToken(userId, now);

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("access_token", accessTokenDto.getToken())
                .queryParam("refresh_token", refreshTokenDto.getToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request,
            HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAllAuthorizationRequestCookies(request, response);
    }

}
