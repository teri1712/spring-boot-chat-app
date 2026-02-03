package com.decade.practice.infra.security.jwt;

import com.decade.practice.application.usecases.TokenStore;
import com.decade.practice.dto.TokenCredential;
import com.decade.practice.infra.security.TokenService;
import com.decade.practice.infra.security.models.UserClaims;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService implements TokenService {

    private static final long ONE_WEEK = 7L * 24 * 60 * 60 * 1000L;
    private static final long FIFTEEN_MINUTES = 15 * 60 * 1000L;
    private static final String TOKEN_KEY_SPACE = "JWT_TOKENS";

    private final TokenStore tokenStore;
    private final String key;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtService(
            TokenStore tokenStore, @Value("${jwt.secret}") String secret
    ) {
        this.tokenStore = tokenStore;
        this.key = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    private static String generateKey(String username) {
        return TOKEN_KEY_SPACE + ":" + username;
    }

    @Override
    public void validate(String refreshToken) throws AccessDeniedException {
        UserClaims claims;
        try {
            claims = decodeToken(refreshToken);
        } catch (Exception e) {
            throw new AccessDeniedException("Token expired", e);
        }
        String key = generateKey(claims.getUsername());
        if (!tokenStore.has(key, refreshToken)) {
            throw new AccessDeniedException("Token expired");
        }
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

    private String encodeToken(UserClaims user, long duration, long at) {
        Map<String, Object> claims = objectMapper.convertValue(
                user,
                new TypeReference<Map<String, Object>>() {
                }
        );
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setIssuedAt(new Date(at))
                .setExpiration(new Date(at + duration))
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    @Override
    public TokenCredential create(UserClaims claims, String refreshToken) {
        String refresh = refreshToken;
        long currentTime = System.currentTimeMillis();

        String accessToken = encodeToken(claims, FIFTEEN_MINUTES, currentTime);
        if (refresh == null) {
            refresh = encodeToken(claims, ONE_WEEK, currentTime);
            tokenStore.add(claims.getUsername(), refresh);
        }

        return new TokenCredential(
                accessToken,
                refresh,
                FIFTEEN_MINUTES,
                currentTime
        );
    }
}