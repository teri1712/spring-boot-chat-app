package com.decade.practice.online;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Configuration class for Redis repositories.
 */
@Configuration
class RedisRepositoriesConfig {
      private static final String KEYSPACE = "ONLINE_USERS";
      private static final int TTL = 5 * 60;

      @Bean
      public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connFactory) {
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(connFactory);
            template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
            return template;
      }
}
