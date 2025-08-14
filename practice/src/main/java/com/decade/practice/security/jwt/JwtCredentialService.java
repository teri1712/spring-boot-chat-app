package com.decade.practice.security.jwt;

import com.decade.practice.model.TokenCredential;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.security.TokenCredentialService;
import com.decade.practice.security.model.UserClaims;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class JwtCredentialService implements TokenCredentialService {

        private static final long ONE_WEEK = 7L * 24 * 60 * 60 * 1000L;
        private static final long ONE_MONTH = 30 * 24 * 60 * 60 * 1000L;
        private static final String TOKEN_KEY_SPACE = "JWT_TOKENS";

        private final StringRedisTemplate redisTemplate;
        private final String key;
        private final ObjectMapper objectMapper = new ObjectMapper();

        public JwtCredentialService(
                @Value("${credential.jwt.secret}") String secret,
                StringRedisTemplate redisTemplate
        ) {
                this.redisTemplate = redisTemplate;
                this.key = Base64.getEncoder().encodeToString(secret.getBytes());
        }

        private static String generateKey(String username) {
                return TOKEN_KEY_SPACE + ":" + username;
        }

        @Override
        public void add(String username, String refreshToken) {
                String key = generateKey(username);
                redisTemplate.opsForSet().add(key, refreshToken);
                redisTemplate.expire(key, ONE_MONTH, TimeUnit.MILLISECONDS);
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
                Set<String> activeTokens = redisTemplate.opsForSet().members(key);
                if (activeTokens == null || !activeTokens.contains(refreshToken)) {
                        throw new AccessDeniedException("Token expired");
                }
        }

        @Override
        public List<String> evict(String username) {
                String key = generateKey(username);
                List<String> deletedTokens = get(username);
                for (String value : deletedTokens) {
                        redisTemplate.opsForSet().remove(key, value);
                }
                return deletedTokens;
        }

        @Override
        public void evict(String username, String refreshToken) {
                String key = generateKey(username);
                redisTemplate.opsForSet().remove(key, refreshToken);
        }

        @Override
        public List<String> get(String username) {
                String key = generateKey(username);
                Set<String> members = redisTemplate.opsForSet().members(key);
                if (members == null) {
                        return Collections.emptyList();
                }
                return new ArrayList<>(members);
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

        private String encodeToken(UserClaims user, long expiration, long at) {
                Map<String, Object> claims = objectMapper.convertValue(
                        user,
                        new TypeReference<Map<String, Object>>() {
                        }
                );
                return Jwts.builder()
                        .setHeaderParam("typ", "JWT")
                        .setHeaderParam("alg", "HS256")
                        .setClaims(claims)
                        .signWith(SignatureAlgorithm.HS256, key)
                        .compact();
        }

        @Override
        public TokenCredential create(UserClaims claims, String refreshToken) {
                String refresh = refreshToken;
                long currentTime = System.currentTimeMillis();

                String accessToken = encodeToken(claims, ONE_WEEK, currentTime);
                if (refresh == null) {
                        refresh = encodeToken(claims, ONE_MONTH, currentTime);
                        add(claims.getUsername(), refresh);
                }

                return new TokenCredential(
                        accessToken,
                        refresh,
                        ONE_WEEK,
                        currentTime
                );
        }

        @Override
        public TokenCredential create(User user, String refreshToken) {
                UserClaims claims = new UserClaims(user);
                return create(claims, refreshToken);
        }
}