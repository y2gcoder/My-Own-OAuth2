package com.y2gcoder.auth.common.ui;

import com.y2gcoder.auth.auth.application.InvalidAccessTokenException;
import com.y2gcoder.auth.auth.infra.JwtTokenProvider;
import com.y2gcoder.auth.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class LoggedInUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoggedInUserId.class)
                && parameter.getParameterType().equals(UserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String authorizationHeader = webRequest.getHeader("Authorization");
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(
                "Bearer ")) {
            throw new IllegalArgumentException(
                    "Authentication required. Please provide a valid token.");
        }

        String accessToken = authorizationHeader.split("Bearer ")[1];

        return getUsernameBy(accessToken);
    }

    private UserId getUsernameBy(String accessToken) {
        try {
            jwtTokenProvider.validateToken(accessToken);
            String username = jwtTokenProvider.getUsernameFrom(accessToken);
            return UserId.of(username);
        } catch (Exception e) {
            throw new InvalidAccessTokenException(e);
        }
    }
}
