package com.decade.practice.users.adapter;

import com.decade.practice.users.application.ports.out.TokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisBasedTokenStore implements TokenStore {
    private static final String TOKEN_KEY_SPACE = "JWT_TOKENS";

    private final StringRedisTemplate redisTemplate;

    @Value("${token.refresh.duration}")
    Duration refreshDue;

    private static String generateKey(String username) {
        return TOKEN_KEY_SPACE + ":" + username;
    }

    @Override
    public void add(String username, String... refreshTokens) {
        String key = generateKey(username);
        redisTemplate.execute(new SessionCallback<Object>() {
            @Nullable
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                StringRedisTemplate template = (StringRedisTemplate) operations;
                template.opsForSet().add(key, refreshTokens);
                template.expire(key, refreshDue);
                return null;
            }
        });
    }

    @Override
    public Long size(String username) {
        return redisTemplate.opsForSet().size(generateKey(username));
    }

    @Override
    public Set<String> get(String username) {
        return redisTemplate.opsForSet().members(generateKey(username));
    }

    @Override
    public void evict(String username) {
        String key = generateKey(username);
        redisTemplate.delete(key);
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


}