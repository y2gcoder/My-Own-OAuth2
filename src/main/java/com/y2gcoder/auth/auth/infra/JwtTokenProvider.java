package com.y2gcoder.auth.auth.infra;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtTokenProvider {
    private final Key key;

    public JwtTokenProvider(String secretString) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
    }


}
