package com.decade.practice.application.services;

import com.decade.practice.application.usecases.TokenStore;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class RedisBasedTokenStore implements TokenStore {
    private static final long ONE_MONTH = 30 * 24 * 60 * 60 * 1000L;
    private static final String TOKEN_KEY_SPACE = "JWT_TOKENS";

    private final StringRedisTemplate redisTemplate;

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
    public List<String> evict(String username) {
        String key = generateKey(username);
        List<String> deletedTokens = getTokens(username);
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
    public boolean has(String username, String refreshToken) {
        String key = generateKey(username);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, refreshToken));
    }

    private List<String> getTokens(String username) {
        String key = generateKey(username);
        Set<String> tokens = redisTemplate.opsForSet().members(key);
        if (tokens == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(tokens);
    }


}