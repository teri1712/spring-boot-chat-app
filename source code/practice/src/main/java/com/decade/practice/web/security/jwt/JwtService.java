package com.decade.practice.web.security.jwt;

import com.decade.practice.web.security.TokenService;
import com.decade.practice.web.security.UserClaims;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
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
        Claims claims = parse(token).getBody();
        return objectMapper.convertValue(claims, UserClaims.class);
    }

    @Override
    public String encodeToken(UserClaims user, Long duration) {
        Map<String, Object> claims = objectMapper.convertValue(
                user,
                new TypeReference<Map<String, Object>>() {
                }
        );
        Date at = new Date();
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setIssuedAt(at)
                .setExpiration(new Date(at.getTime() + duration))
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

}