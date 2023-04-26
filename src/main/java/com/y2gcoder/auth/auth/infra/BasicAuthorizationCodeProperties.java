package com.y2gcoder.auth.auth.infra;

import lombok.Getter;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Validated
@ConfigurationProperties("basic.authorization-code")
public class BasicAuthorizationCodeProperties {
    @DurationMin(minutes = 1)
    @DurationMax(minutes = 10)
    private final Duration expiration;

    @ConstructorBinding
    public BasicAuthorizationCodeProperties(Duration expiration) {
        this.expiration = expiration;
    }
}
