package com.y2gcoder.auth.oauth.application.dto;

import com.y2gcoder.auth.oauth.domain.OAuth2Provider;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public abstract String getId();

    public abstract String getEmail();

    public abstract String getName();

    public abstract String getProfileImageUrl();

    public abstract OAuth2Provider getProvider();
}
