package com.y2gcoder.auth.auth.infra;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtTokenProvider {
    private final Key key;
    private final Long expiration;

    public JwtTokenProvider(String secretString, Long expiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
        this.expiration = expiration;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .signWith(key)
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.warn("Jwt Token is Expired");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("Jwt Token is invalid");
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Jwt Token is invalid");
            throw e;
        } catch (SignatureException e) {
            log.warn("Jwt signature validation fails");
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("Jwt Token is invalid");
            throw e;
        }

    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody().getSubject();
    }

    public LocalDateTime getExpiration(String token) {
        Date tokenExpirationDate = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody().getExpiration();
        return tokenExpirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
