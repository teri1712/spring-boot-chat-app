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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeliveringManagement {

    final RoomRepository rooms;
    final ApplicationEventPublisher publisher;
    final LogBroadCaster broadcaster;
    final ConversationRepository conversations;

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    void on(MessageCreated event) {
        String chatId = event.chatId();
        Room room = rooms.findByChatId(chatId).orElseThrow();
        if (room.getParticipantCount() < 20) {
            broadcaster.broadcastInsert(event, conversations.findByChatId(chatId));
        } else if (room.getParticipantCount() <= 100) {
            for (int i = 0; i < room.getParticipantCount(); i += 20) {
                publisher.publishEvent(new BatchInsertionEvent(i, i + 20, event));
            }
        }
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    void on(MessageUpdated event) {
        String chatId = event.chatId();
        Room room = rooms.findByChatId(chatId).orElseThrow();
        if (room.getParticipantCount() < 20) {
            broadcaster.broadcastUpdate(event, conversations.findByChatId(chatId));
        } else if (room.getParticipantCount() <= 100) {
            for (int i = 0; i < room.getParticipantCount(); i += 20) {
                publisher.publishEvent(new BatchUpdateEvent(i, i + 20, event));
            }
        }
    }
}
