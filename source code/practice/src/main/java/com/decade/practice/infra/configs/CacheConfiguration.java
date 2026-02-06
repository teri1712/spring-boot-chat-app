package com.decade.practice.infra.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private static RedisCacheConfiguration defaults(ObjectMapper objectMapper, long ttlSeconds) {

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttlSeconds));
    }

    private static CacheManager build(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper,
                                      long ttlSeconds) {
        return RedisCacheManager.builder(
                        RedisCacheWriter.lockingRedisCacheWriter(connectionFactory))
                .cacheDefaults(defaults(objectMapper, ttlSeconds))
                .build();
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        return build(connectionFactory, objectMapper, 5 * 60L);
    }
}