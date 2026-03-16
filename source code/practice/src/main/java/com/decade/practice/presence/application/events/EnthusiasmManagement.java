package com.decade.practice.presence.application.events;

import com.decade.practice.engagement.domain.events.StalkEvent;
import com.decade.practice.inbox.domain.events.RoomCreated;
import com.decade.practice.inbox.domain.events.RoomEventCreated;
import com.decade.practice.live.domain.events.JoinerJoined;
import com.decade.practice.live.domain.events.JoinerTyped;
import com.decade.practice.presence.application.ports.out.ScoreEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnthusiasmManagement {

      private final ScoreEngine scoreEngine;

      @ApplicationModuleListener
      public void on(RoomEventCreated event) {
            UUID userId = event.getSenderId();
            String chatId = event.getChatId();

            scoreEngine.incScore(chatId, userId.toString());

      }

      @ApplicationModuleListener
      public void on(RoomCreated event) {
            String chatId = event.chatId();

            event.representatives().forEach(participant ->
                      scoreEngine.incScore(chatId, participant.toString()));
      }

      @ApplicationModuleListener
      public void on(JoinerTyped event) {
            String chatId = event.chatId();
            String userId = event.userId().toString();
            scoreEngine.incScore(chatId, userId);
      }

      @ApplicationModuleListener
      public void on(JoinerJoined event) {
            String chatId = event.chatId();
            String userId = event.userId().toString();
            scoreEngine.incScore(chatId, userId);
      }

      @ApplicationModuleListener
      public void on(StalkEvent event) {
            scoreEngine.incScore(event.senderId().toString(), event.receiverId().toString());
      }
}
