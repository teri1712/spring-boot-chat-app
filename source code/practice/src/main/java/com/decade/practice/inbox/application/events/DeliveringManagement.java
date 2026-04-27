package com.decade.practice.inbox.application.events;

import com.decade.practice.inbox.application.ports.out.*;
import com.decade.practice.inbox.domain.Room;
import com.decade.practice.inbox.domain.events.BatchInsertionEvent;
import com.decade.practice.inbox.domain.events.BatchUpdateEvent;
import com.decade.practice.inbox.domain.events.MessageCreated;
import com.decade.practice.inbox.domain.events.MessageUpdated;
import com.decade.practice.inbox.domain.services.ConversationInfoService;
import com.decade.practice.inbox.dto.mapper.InboxLogMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeliveringManagement extends LogBroadCast {
    private final RoomRepository rooms;
    private final ApplicationEventPublisher publisher;

    public DeliveringManagement(LogRepository logs, LookUpRegistry lookUpRegistry, ConversationRepository conversations, DeliveryService deliveryService, InboxLogMapper inboxLogMapper, ConversationInfoService conversationInfoService, RoomRepository rooms, ApplicationEventPublisher publisher) {
        super(logs, lookUpRegistry, conversations, deliveryService, inboxLogMapper, conversationInfoService);
        this.rooms = rooms;
        this.publisher = publisher;
    }


    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    void on(MessageCreated event) {
        Long roomId = event.roomId();
        Room room = rooms.findById(roomId).orElseThrow();
        if (room.getParticipantCount() < 20) {
            broadcastInsert(event, conversations.findByRoomId(roomId));
        } else if (room.getParticipantCount() <= 100) {
            for (int i = 0; i < room.getParticipantCount(); i += 20) {
                publisher.publishEvent(new BatchInsertionEvent(i, i + 20, event));
            }
        }
    }

    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    void on(MessageUpdated event) {

        Long roomId = event.roomId();
        Room room = rooms.findById(roomId).orElseThrow();

        if (room.getParticipantCount() < 20) {
            broadcastUpdate(event, conversations.findByRoomId(roomId));
        } else if (room.getParticipantCount() <= 100) {
            for (int i = 0; i < room.getParticipantCount(); i += 20) {
                publisher.publishEvent(new BatchUpdateEvent(i, i + 20, event));
            }
        }
    }
}
