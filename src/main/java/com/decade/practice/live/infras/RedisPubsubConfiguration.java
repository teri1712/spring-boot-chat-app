package com.decade.practice.live.infras;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisPubsubConfiguration {

      @Bean
      public RedisMessageListenerContainer redisMessageListenerContainer(
                RedisConnectionFactory connectionFactory) {
            RedisMessageListenerContainer container =
                      new RedisMessageListenerContainer();

            container.setConnectionFactory(connectionFactory);

            return container;
      }

}
