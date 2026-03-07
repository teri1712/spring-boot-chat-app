package com.decade.practice.users.application.services;

import com.decade.practice.users.application.ports.out.TokenStore;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
      public void add(String username, String... refreshTokens) {
            String key = generateKey(username);
            redisTemplate.opsForSet().add(key, refreshTokens);
            redisTemplate.expire(key, ONE_MONTH, TimeUnit.MILLISECONDS);
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