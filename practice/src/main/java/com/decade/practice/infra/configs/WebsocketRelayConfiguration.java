package com.decade.practice.infra.configs;

import com.decade.practice.application.events.EventSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class WebsocketRelayConfiguration {

    @Bean
    public ChannelTopic queueTopic() {
        return new ChannelTopic("queue");
    }

    @Bean
    public ChannelTopic chatTopic() {
        return new ChannelTopic("chat");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            EventSubscriber subscriber
    ) {
        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, queueTopic());
        container.addMessageListener(subscriber, chatTopic());

        return container;
    }

}
