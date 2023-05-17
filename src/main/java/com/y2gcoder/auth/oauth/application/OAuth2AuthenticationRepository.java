package com.y2gcoder.auth.oauth.application;

import com.y2gcoder.auth.oauth.domain.OAuth2Authentication;
import com.y2gcoder.auth.oauth.domain.OAuth2AuthenticationId;
import java.util.Optional;

public interface OAuth2AuthenticationRepository {

    OAuth2Authentication save(OAuth2Authentication oAuth2Authentication);

    OAuth2AuthenticationId nextOAuth2AuthenticationId();

    Optional<OAuth2Authentication> findByProviderId(String providerId);

    void delete(OAuth2Authentication oAuth2Authentication);
}
