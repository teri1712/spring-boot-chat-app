package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.*;
import com.decade.practice.inbox.application.ports.out.projection.ConversationView;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.mapper.MessageStateResponseMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BatchRoundRobinSaver extends LogBroadCast {

    public BatchRoundRobinSaver(LogRepository logs, LookUpRegistry lookUpRegistry, ConversationRepository conversations, DeliveryService deliveryService, MessageStateResponseMapper messageStateMapper, ConversationInfoService conversationInfoService) {
        super(logs, lookUpRegistry, conversations, deliveryService, messageStateMapper, conversationInfoService);
    }

    @KafkaListener(topics = "batch-insertion-placed", groupId = "inbox", concurrency = "4")
    void on(BatchInsertionEvent event) {
        List<ConversationView> convos = conversations.findByChatIdBetweenRoundRobin(event.insertion().chatId(), event.lower(), event.upper());
        broadcastInsert(event.insertion(), convos);
    }

    @KafkaListener(topics = "batch-update-placed", groupId = "inbox", concurrency = "4")
    void on(BatchUpdateEvent event) {
        List<ConversationView> convos = conversations.findByChatIdBetweenRoundRobin(event.update().chatId(), event.lower(), event.upper());
        broadcastUpdate(event.update(), convos);
    }
}
