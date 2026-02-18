package com.decade.practice.threads.application.services;

import com.decade.practice.threads.application.ports.out.DeliveryService;
import com.decade.practice.threads.dto.EventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    @Value("${broker.topics.queue}")
    private String queueTopic;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void send(EventResponse event) {
        redisTemplate.convertAndSend(queueTopic + ":" + event.getOwnerId(), event);
    }

}
