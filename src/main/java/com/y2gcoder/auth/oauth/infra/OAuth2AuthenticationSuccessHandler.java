package com.y2gcoder.auth.oauth.infra;

import com.y2gcoder.auth.auth.application.RefreshTokenRepository;
import com.y2gcoder.auth.auth.domain.RefreshToken;
import com.y2gcoder.auth.auth.domain.RefreshTokenId;
import com.y2gcoder.auth.auth.domain.RefreshTokenProvider;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
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
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    // TODO 바운디드 컨텍스트를 위해 리팩토링 필요
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenProvider refreshTokenProvider;

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

        String accessToken = jwtTokenProvider.generateToken(userId.getValue(), now);
        RefreshToken refreshToken = createRefreshToken(now, userId);

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("access_token", accessToken)
                .queryParam("refresh_token", refreshToken.getToken())
                .build().toUriString();
    }

    private RefreshToken createRefreshToken(LocalDateTime currentTime, UserId ownerId) {
        RefreshTokenId refreshTokenId = refreshTokenRepository.nextRefreshTokenId();
        LocalDateTime refreshTokenExpirationTime = refreshTokenProvider.getExpirationTime(
                currentTime);

        return refreshTokenRepository.save(new RefreshToken(refreshTokenId,
                refreshTokenProvider.generateToken(),
                ownerId,
                refreshTokenExpirationTime, currentTime));
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request,
            HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAllAuthorizationRequestCookies(request, response);
    }

}
