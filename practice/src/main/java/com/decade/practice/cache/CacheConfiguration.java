package com.decade.practice.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration {

        public static CacheManager cacheManager(RedisConnectionFactory connectionFactory, long seconds) {
                return RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter(connectionFactory))
                        .cacheDefaults(
                                RedisCacheConfiguration.defaultCacheConfig()
                                        .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                                        )
                                        .entryTtl(Duration.ofSeconds(seconds))
                        ).build();
        }

        @Bean
        @Primary
        public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
                return cacheManager(connectionFactory, 5 * 60L);
        }
}