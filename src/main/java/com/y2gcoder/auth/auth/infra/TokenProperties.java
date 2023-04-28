package com.y2gcoder.auth.auth.infra;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Validated
@ConfigurationProperties("token")
public class TokenProperties {
    private final Access access;
    private final Refresh refresh;

    @ConstructorBinding
    public TokenProperties(@DefaultValue Access access, @DefaultValue Refresh refresh) {
        this.access = access;
        this.refresh = refresh;
    }

    @Getter
    public static class Access {
        @NotEmpty
        private final String secret;
        @DurationMin(minutes = 1)
        private final Duration expiration;

        @ConstructorBinding
        public Access(String secret, Duration expiration) {
            this.secret = secret;
            this.expiration = expiration;
        }
    }

    @Getter
    public static class Refresh {
        @DurationMin(minutes = 15)
        private final Duration expiration;

        @ConstructorBinding
        public Refresh(Duration expiration) {
            this.expiration = expiration;
        }
    }
}
