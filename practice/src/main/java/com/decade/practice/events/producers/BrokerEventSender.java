package com.decade.practice.events.producers;

import com.decade.practice.application.usecases.EventSender;
import com.decade.practice.dto.EventDto;
import com.decade.practice.dto.TypeEventDto;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@AllArgsConstructor
public class BrokerEventSender implements EventSender {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic queueTopic;
    private final ChannelTopic chatTopic;

    @Override
    public void send(EventDto event) {
        if (TransactionSynchronizationManager.isSynchronizationActive() && TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    redisTemplate.convertAndSend(queueTopic.getTopic(), event);
                }
            });
        } else {
            redisTemplate.convertAndSend(queueTopic.getTopic(), event);
        }
    }

    @Override
    public void send(TypeEventDto typeEvent) {
        // accessor.getMessageHeaders()
        redisTemplate.convertAndSend(chatTopic.getTopic(), typeEvent);
    }
}
