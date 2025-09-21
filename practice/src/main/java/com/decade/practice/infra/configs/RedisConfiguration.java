package com.decade.practice.infra.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Configuration class for Redis repositories.
 */
@Configuration
public class RedisConfiguration {

        @Bean
        public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connFactory) {
                RedisTemplate<Object, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connFactory);
                template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
                return template;
        }
}
