package com.decade.practice.presence.application.events;

import com.decade.practice.engagement.domain.events.StalkEvent;
import com.decade.practice.inbox.domain.events.RoomCreated;
import com.decade.practice.inbox.domain.events.RoomEventCreated;
import com.decade.practice.live.domain.events.JoinerJoined;
import com.decade.practice.live.domain.events.JoinerTyped;
import com.decade.practice.presence.application.ports.out.ScoreEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnthusiasmManagement {

    private final ScoreEngine scoreEngine;

    @Async
    @TransactionalEventListener(id = "enthus_room_event")
    public void on(RoomEventCreated event) {
        UUID userId = event.getSenderId();
        String chatId = event.getChatId();

        scoreEngine.incScore(chatId, userId.toString());

    }

    @Async
    @TransactionalEventListener(id = "enthus_room_created")
    public void on(RoomCreated event) {
        String chatId = event.chatId();

        event.participants().forEach(participant ->
            scoreEngine.incScore(chatId, participant.toString()));
    }

    @Async
    @EventListener(id = "enthus_user_typed")
    public void on(JoinerTyped event) {
        String chatId = event.chatId();
        String userId = event.userId().toString();
        scoreEngine.incScore(chatId, userId);
    }

    @Async
    @EventListener(id = "enthus_user_joined")
    public void on(JoinerJoined event) {
        String chatId = event.chatId();
        String userId = event.userId().toString();
        scoreEngine.incScore(chatId, userId);
    }

    @Async
    @EventListener(id = "enthus_user_stalked")
    public void on(StalkEvent event) {
        scoreEngine.incScore(event.senderId().toString(), event.receiverId().toString());
    }
}