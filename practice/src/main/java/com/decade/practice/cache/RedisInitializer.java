package com.decade.practice.cache;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(-1)
public class RedisInitializer implements ApplicationRunner {
        private final RedisTemplate redisTemplate;

        public RedisInitializer(RedisTemplate redisTemplate) {
                this.redisTemplate = redisTemplate;
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {
                redisTemplate.getConnectionFactory().getConnection().flushDb();
        }
}
