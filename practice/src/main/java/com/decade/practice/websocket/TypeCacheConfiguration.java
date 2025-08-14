package com.decade.practice.websocket;

import com.decade.practice.cache.CacheConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class TypeCacheConfiguration {

        public static final String TYPE_REPOSITORY_CACHE_MANAGER = "TYPE_REPOSITORY_CACHE_MANAGER";


        @Bean(TYPE_REPOSITORY_CACHE_MANAGER)
        public CacheManager typeRepoCacheManager(RedisConnectionFactory connectionFactory) {
                return CacheConfiguration.cacheManager(connectionFactory, 2);
        }
}
