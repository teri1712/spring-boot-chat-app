package com.decade.practice.inbox.application.events;

import com.decade.practice.engagement.domain.events.PreferenceChanged;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.application.ports.out.MessageRepository;
import com.decade.practice.inbox.domain.Preference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PreferenceListener {


      private final MessageRepository messages;
      private final ConversationRepository conversations;


      @ApplicationModuleListener
      public void on(PreferenceChanged event) {

            long rowsAffected = conversations.updateRoomNameAndRoomAvatar(event.getChatId(), event.getRoomName(), event.getRoomAvatar());
            log.info("Updated {} rows for chat {}", rowsAffected, event.getChatId());


            messages.save(new Preference(
                      UUID.randomUUID(),
                      event.getMakerId(),
                      event.getChatId(),
                      event.getCreatedAt(),
                      event.getIconId(),
                      event.getRoomAvatar(),
                      event.getRoomName(),
                      event.getTheme()));
      }
}
