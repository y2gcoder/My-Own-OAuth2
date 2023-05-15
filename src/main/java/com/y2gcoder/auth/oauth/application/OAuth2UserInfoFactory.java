package com.y2gcoder.auth.oauth.application;

import com.y2gcoder.auth.oauth.application.dto.GoogleOAuth2UserInfo;
import com.y2gcoder.auth.oauth.application.dto.KakaoOAuth2UserInfo;
import com.y2gcoder.auth.oauth.application.dto.OAuth2UserInfo;
import com.y2gcoder.auth.oauth.domain.OAuth2Provider;
import java.util.Map;

public class OAuth2UserInfoFactory {


    public static OAuth2UserInfo getOAuth2UserInfoBy(String registrationId,
            Map<String, Object> attributes) {
        return switch (OAuth2Provider.getByRegistrationId(registrationId)) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            default -> throw new UnsupportedOAuth2ProviderException(registrationId);
        };
    }
}
