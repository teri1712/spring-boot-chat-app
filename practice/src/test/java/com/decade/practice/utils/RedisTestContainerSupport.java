package com.decade.practice.utils;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class RedisTestContainerSupport {

        @Container
        protected static final GenericContainer<?> REDIS = new GenericContainer<>("redis:5.0.3-alpine")
                .withExposedPorts(6379);

        static {
                REDIS.start();
        }

        @DynamicPropertySource
        static void registerRedisProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.data.redis.host", REDIS::getHost);
                registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
                registry.add("spring.jpa.properties.hibernate.cache.use_second_level_cache", () -> false);

                // Keep client type default (lettuce) as defined in application.properties
        }
}
