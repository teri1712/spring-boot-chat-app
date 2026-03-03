package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatCreators;
import com.decade.practice.engagement.domain.ChatPolicy;
import com.decade.practice.engagement.domain.Preference;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupChatFactory implements ChatIdentifierMaker {

      @Override
      public String make(ChatCreators creators) {
            return UUID.randomUUID().toString();
      }

      public Chat create(@NotNull ChatCreators creators, Integer maximumParticipants, String roomName) {
            String chatId = make(creators);
            return new Chat(creators, chatId, new Preference(1, roomName, null, null), new ChatPolicy(maximumParticipants));
      }
}
