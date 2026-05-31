package com.decade.practice.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.data.redis.core.StringRedisTemplate;

@RequiredArgsConstructor
@TestComponent
public class RedisSetTest implements TestDataSet {
    private final StringRedisTemplate redisTemplate;

    @Override
    public void clean() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
