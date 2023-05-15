package com.y2gcoder.auth.oauth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.y2gcoder.auth.oauth.application.dto.GoogleOAuth2UserInfo;
import com.y2gcoder.auth.oauth.application.dto.OAuth2UserInfo;
import com.y2gcoder.auth.oauth.domain.OAuth2Provider;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuth2UserInfoFactoryTest {


    @DisplayName("registrationId와 OAuth2User의 attributes로 OAuth2UserInfo를 만들 수 있다.")
    @Test
    void getOAuth2UserInfoBy() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("iss", "https://accounts.google.com");
        attributes.put("azp", "1234987819200.apps.googleusercontent.com");
        attributes.put("aud", "1234987819200.apps.googleusercontent.com");
        attributes.put("sub", "10769150350006150715113082367");
        attributes.put("at_hash", "HK6E_P6Dh8Y93mRNtsDB1Q");
        attributes.put("hd", "example.com");
        attributes.put("email", "jsmith@example.com");
        attributes.put("email_verified", "true");
        attributes.put("iat", 1353601026);
        attributes.put("exp", 1353604926);
        attributes.put("nonce", "0394852-3190485-2490358");
        attributes.put("picture", "test picture url");
        attributes.put("name", "John Doe");

        String registrationId = OAuth2Provider.GOOGLE.getRegistrationId();

        // when
        OAuth2UserInfo result = OAuth2UserInfoFactory.getOAuth2UserInfoBy(registrationId,
                attributes);

        // then
        assertThat(result).isExactlyInstanceOf(GoogleOAuth2UserInfo.class);
        assertThat(result.getId()).isEqualTo(attributes.get("sub"));
        assertThat(result.getEmail()).isEqualTo(attributes.get("email"));
        assertThat(result.getName()).isEqualTo(attributes.get("name"));
        assertThat(result.getProfileImageUrl()).isEqualTo(attributes.get("picture"));

    }

    @DisplayName("잘못된 registrationId로 OAuth2UserInfo를 생성할 수 없다.")
    @Test
    void getOAuth2UserInfoByWhenRegistrationIdIsWrong() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("iss", "https://accounts.google.com");
        attributes.put("azp", "1234987819200.apps.googleusercontent.com");
        attributes.put("aud", "1234987819200.apps.googleusercontent.com");
        attributes.put("sub", "10769150350006150715113082367");
        attributes.put("at_hash", "HK6E_P6Dh8Y93mRNtsDB1Q");
        attributes.put("hd", "example.com");
        attributes.put("email", "jsmith@example.com");
        attributes.put("email_verified", "true");
        attributes.put("iat", 1353601026);
        attributes.put("exp", 1353604926);
        attributes.put("nonce", "0394852-3190485-2490358");
        attributes.put("picture", "test picture url");
        attributes.put("name", "John Doe");

        String registrationId = "wrong";

        // expected
        assertThatThrownBy(() -> OAuth2UserInfoFactory.getOAuth2UserInfoBy(registrationId,
                attributes))
                .isInstanceOf(UnsupportedOAuth2ProviderException.class)
                .hasMessage(String.format("Unsupported OAuth2 provider with registrationId: '%s'",
                        registrationId));

    }
}