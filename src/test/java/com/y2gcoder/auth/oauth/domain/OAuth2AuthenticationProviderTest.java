package com.y2gcoder.auth.oauth.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OAuth2AuthenticationProviderTest {

    @DisplayName("registrationId로 인증 프로바이더 타입을 불러올 수 있다.")
    @Test
    void getByRegistrationId() {
        // given
        String registrationId = OAuth2Provider.GOOGLE.getRegistrationId();

        // when
        OAuth2Provider result = OAuth2Provider.getByRegistrationId(registrationId);

        //then
        assertThat(result)
                .isEqualByComparingTo(OAuth2Provider.GOOGLE);
    }

    @DisplayName("지원하지 않는 registrationId를 사용하면 프로바이더 타입을 불러올 수 없다.")
    @Test
    void getByRegistrationIdWithUnsupportedRegistrationId() {
        // given
        String registrationId = "anonymous";

        // when
        OAuth2Provider result = OAuth2Provider.getByRegistrationId(registrationId);

        //then
        assertThat(result).isNull();

    }
}