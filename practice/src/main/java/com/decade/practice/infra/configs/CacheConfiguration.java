package com.decade.practice.infra.configs;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
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

        static class ByteBuddyRemovalGenericJackson2JsonRedisSerializer extends GenericJackson2JsonRedisSerializer {

                // fuck hibernate
                ByteBuddyRemovalGenericJackson2JsonRedisSerializer() {
                        super();
                        getObjectMapper().registerModule(new Hibernate6Module());
                }

        }

        private static RedisCacheConfiguration defaults(long ttlSeconds) {
                return RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new ByteBuddyRemovalGenericJackson2JsonRedisSerializer()
                                ))
                        .entryTtl(Duration.ofSeconds(ttlSeconds));
        }

        private static CacheManager build(RedisConnectionFactory connectionFactory,
                                          long ttlSeconds) {
                return RedisCacheManager.builder(
                                RedisCacheWriter.lockingRedisCacheWriter(connectionFactory))
                        .cacheDefaults(defaults(ttlSeconds))
                        .build();
        }

        @Bean
        @Primary
        public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
                return build(connectionFactory, 5 * 60L);
        }

        @Bean("TYPE_EVENTS_CACHE_MANAGER")
        public CacheManager typeRepoCacheManager(RedisConnectionFactory connectionFactory) {
                return build(connectionFactory, 2);
        }
}