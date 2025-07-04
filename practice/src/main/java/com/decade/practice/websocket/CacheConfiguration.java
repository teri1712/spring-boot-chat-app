package com.decade.practice.websocket;

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

/**
 * Configuration class for Redis caching
 */
@Configuration
@EnableCaching
class CacheConfiguration {

      /**
       * Creates a cache manager with the specified TTL
       *
       * @param connectionFactory the Redis connection factory
       * @param seconds           the TTL in seconds
       * @return the cache manager
       */
      public CacheManager cacheManager(RedisConnectionFactory connectionFactory, long seconds) {
            return RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter(connectionFactory))
                  .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig()
                              .serializeValuesWith(
                                    RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                              )
                              .entryTtl(Duration.ofSeconds(seconds))
                  ).build();
      }

      /**
       * Creates a cache manager for type repository with a short TTL (2 seconds)
       *
       * @param connectionFactory the Redis connection factory
       * @return the cache manager
       */
      @Bean(CacheConstants.TYPE_REPOSITORY_CACHE_MANAGER)
      public CacheManager typeRepoCacheManager(RedisConnectionFactory connectionFactory) {
            return cacheManager(connectionFactory, 2);
      }

      /**
       * Creates a primary cache manager with a longer TTL (5 minutes)
       *
       * @param connectionFactory the Redis connection factory
       * @return the cache manager
       */
      @Bean
      @Primary
      public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
            return cacheManager(connectionFactory, 5 * 60L);
      }
}
