package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.ports.out.DeliveryService;
import com.decade.practice.inbox.application.ports.out.LookUpRegistry;
import com.decade.practice.inbox.domain.messages.InboxLogMessage;
import com.decade.practice.inbox.dto.InboxLogMessageWithPartnerDto;
import com.decade.practice.inbox.dto.mapper.InboxLogMessageWithPartnerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InboxLogDelivery implements DeliveryService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final LookUpRegistry lookUpRegistry;

    @Value("${broker.topics.queue}")
    private String queueTopic;

    private final InboxLogMessageWithPartnerMapper mapper;
    private final LogMessageAggregator aggregator;


    @Override
    public void send(InboxLogMessage message) {
        Set<UUID> userIds = aggregator.aggregate(message).collect(Collectors.toSet());
        InboxLogMessageWithPartnerDto sentMessage = mapper.map(message, message.info(), lookUpRegistry.registerLookUp(userIds));
        redisTemplate.convertAndSend(queueTopic + ":" + message.ownerId(), sentMessage);
    }
}
