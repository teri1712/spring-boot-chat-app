package com.decade.practice.engagement.infra;

import com.decade.practice.engagement.api.DirectMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration {


      private static RedisCacheConfiguration directMappingConfig(ObjectMapper objectMapper, long ttlSeconds) {
            return RedisCacheConfiguration.defaultCacheConfig()
                      .entryTtl(Duration.ofSeconds(ttlSeconds))
                      .serializeValuesWith(SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, DirectMapping.class)));
      }

      @Bean
      private static RedisCacheManagerBuilderCustomizer directMappingCustomizer(ObjectMapper objectMapper) {
            return new RedisCacheManagerBuilderCustomizer() {
                  @Override
                  public void customize(RedisCacheManager.RedisCacheManagerBuilder builder) {
                        builder.withCacheConfiguration("directMapping", directMappingConfig(objectMapper, 5 * 60L));
                  }
            };
      }

}