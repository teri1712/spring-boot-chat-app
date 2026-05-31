package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatCreators;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupChatFactory implements ChatIdentifierMaker {

      @Override
      public String make(ChatCreators creators) {
            return UUID.randomUUID().toString();
      }

      public Chat create(@NotNull ChatCreators creators, Integer maximumParticipants) {
            String chatId = make(creators);
            return new Chat(chatId, maximumParticipants, creators);
      }
}
