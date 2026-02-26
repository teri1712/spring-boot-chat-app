package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.DeliveryService;
import com.decade.practice.inbox.dto.InboxLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InboxLogDelivery implements DeliveryService {
      private final RedisTemplate<String, Object> redisTemplate;

      @Value("${broker.topics.queue}")
      private String queueTopic;


      @Override
      public void send(InboxLogResponse inboxLogCreated) {
            redisTemplate.convertAndSend(queueTopic + ":" + inboxLogCreated.ownerId(), inboxLogCreated);
      }
}
