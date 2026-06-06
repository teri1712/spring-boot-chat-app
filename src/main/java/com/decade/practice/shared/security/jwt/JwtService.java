package com.decade.practice.shared.security.jwt;

import com.decade.practice.shared.security.TokenService;
import com.decade.practice.shared.security.UserClaims;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtService implements TokenService {

    private final String key;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtService(
        @Value("${jwt.secret}") String secret
    ) {
        this.key = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    private Jws<Claims> parse(String token) throws ExpiredJwtException, UnsupportedJwtException,
        MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parser()
            .setSigningKey(key)
            .parseClaimsJws(token);
    }

    @Override
    public UserClaims decodeToken(String token) throws ExpiredJwtException, UnsupportedJwtException,
        MalformedJwtException, SignatureException, IllegalArgumentException {
        Jws<Claims> jwt = parse(token);
        return objectMapper.convertValue(jwt.getBody(), UserClaims.class);
    }

    @Override
    public String encodeToken(UserClaims user, Duration duration) {
        Map<String, Object> claims = objectMapper.convertValue(
            user,
            new TypeReference<Map<String, Object>>() {
            }
        );
        Date at = new Date();
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg", "HS256")
            .setClaims(claims)
            .setIssuedAt(at)
            .setExpiration(new Date(at.getTime() + duration.toMillis()))
            .signWith(SignatureAlgorithm.HS256, key)
            .setId(UUID.randomUUID().toString())
            .compact();
    }

}