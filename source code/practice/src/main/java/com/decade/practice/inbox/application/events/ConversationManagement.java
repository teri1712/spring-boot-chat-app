package com.decade.practice.inbox.application.events;

import com.decade.practice.engagement.dto.events.IntegrationChatCreated;
import com.decade.practice.engagement.dto.events.IntegrationChatSnapshot;
import com.decade.practice.engagement.dto.events.PreferenceIntegrationChatEventPlaced;
import com.decade.practice.inbox.application.ports.out.ConversationRepository;
import com.decade.practice.inbox.domain.Conversation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ConversationManagement {

      private final ConversationRepository conversations;

      @ApplicationModuleListener
      public void on(IntegrationChatCreated event) {
            IntegrationChatSnapshot snapshot = event.getSnapshot();
            String chatId = snapshot.chatId();
            List<Conversation> conversationList = snapshot.participants().stream()
                      .map(participant -> {
                            return new Conversation(chatId, participant, snapshot.roomName(), snapshot.roomAvatar());
                      }).toList();

            conversations.saveAll(conversationList);
      }


      @ApplicationModuleListener
      public void on(PreferenceIntegrationChatEventPlaced prefs) {
            IntegrationChatSnapshot snapshot = prefs.getSnapshot();
            long rowsAffected = conversations.updateRoomNameAndRoomAvatar(snapshot.chatId(), prefs.getRoomName(), prefs.getRoomAvatar());
            log.info("Updated {} rows for chat {}", rowsAffected, snapshot.chatId());
      }

}
