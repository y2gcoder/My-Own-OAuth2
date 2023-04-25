package com.y2gcoder.auth.auth.infra;

import com.y2gcoder.auth.auth.domain.RefreshTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenProperties.class)
public class TokenConfig {

    private final TokenProperties properties;

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(properties.getAccess().getSecret(),
                properties.getAccess().getExpiration());
    }

    @Bean
    public RefreshTokenProvider refreshTokenProvider() {
        return new RefreshTokenProviderImpl(properties.getRefresh().getExpiration());
    }

}

