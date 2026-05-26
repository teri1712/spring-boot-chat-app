package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.LogBroadCaster;
import com.decade.practice.inbox.application.ports.out.RoomRepository;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogManagement {

    final RoomRepository rooms;
    final ApplicationEventPublisher publisher;
    final LogBroadCaster broadcaster;
    final ConversationRepository conversations;


    @Value("${inbox.batch-size}")
    int batchSize;

    @EventListener
    void on(MessageCreated event) {
        String chatId = event.chatId();
        Room room = rooms.findByChatId(chatId).orElseThrow();
        if (room.getParticipantCount() < batchSize) {
            broadcaster.broadcastInsert(event, conversations.findByChatId(chatId));
        } else {
            for (int i = 0; i < room.getParticipantCount(); i += batchSize) {
                publisher.publishEvent(new BatchInsertionEvent(i, i + batchSize, event));
            }
        }
    }

    @EventListener
    void on(MessageUpdated event) {
        String chatId = event.chatId();
        Room room = rooms.findByChatId(chatId).orElseThrow();
        if (room.getParticipantCount() < batchSize) {
            broadcaster.broadcastUpdate(event, conversations.findByChatId(chatId));
        } else {
            for (int i = 0; i < room.getParticipantCount(); i += batchSize) {
                publisher.publishEvent(new BatchUpdateEvent(i, i + batchSize, event));
            }
        }
    }

}
