package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.DeliveryService;
import com.decade.practice.inbox.domain.messages.InboxLogMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InboxLogDelivery implements DeliveryService {
      private final RedisTemplate<String, Object> redisTemplate;

      @Value("${broker.topics.queue}")
      private String queueTopic;


      @Override
      public void send(InboxLogMessage message) {
            redisTemplate.convertAndSend(queueTopic + ":" + message.ownerId(), message);
      }
}
