package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.AuthorizationCodeProvider;
import com.y2gcoder.auth.common.application.Time;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(BasicAuthorizationCodeProperties.class)
public class BasicAuthorizationCodeConfig {

    private final BasicAuthorizationCodeProperties properties;
    private final Time time;

    @Bean
    public AuthorizationCodeProvider basicAuthorizationCodeProvider() {
        return new BasicAuthorizationCodeProvider(time, properties.getExpiration());
    }
}
