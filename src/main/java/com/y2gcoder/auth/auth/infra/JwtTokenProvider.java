package com.y2gcoder.auth.auth.infra;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtTokenProvider {

    private final Key key;
    private final Duration expiration;

    public JwtTokenProvider(String secretString, Duration expiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
        this.expiration = expiration;
    }

    public String generateToken(String username, LocalDateTime issuedAt) {
        return Jwts.builder()
                .signWith(key)
                .setId(UUID.randomUUID().toString())
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(Date.from(issuedAt.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(
                        issuedAt.plus(expiration).atZone(ZoneId.systemDefault()).toInstant()))
                .compact();
    }

    private Jws<Claims> getClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (JwtException e) {
            log.warn("Jwt Token is invalid or expired", e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("Jwt Token is empty", e);
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Jwt token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFrom(String token) {
        Jws<Claims> claims;
        try {
            claims = getClaims(token);
        } catch (ExpiredJwtException e) {
            log.info("Jwt Token is Expired");
            return e.getClaims().getSubject();
        }
        return claims.getBody().getSubject();
    }

    public LocalDateTime getExpiration(String token) {
        Jws<Claims> claims;
        try {
            claims = getClaims(token);
        } catch (ExpiredJwtException e) {
            log.info("Jwt Token is Expired");
            return e.getClaims().getExpiration().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        Date tokenExpirationDate = claims.getBody().getExpiration();
        return tokenExpirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}
