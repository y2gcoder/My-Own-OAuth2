package com.y2gcoder.auth.auth.infra;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;

import javax.crypto.SecretKey;

class JwtTokenProviderTest {

    private JwtTokenProvider sut;
    private String secretString;

    @BeforeEach
    void init() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secretString = Encoders.BASE64.encode(key.getEncoded());
        sut = new JwtTokenProvider(secretString);
    }


}