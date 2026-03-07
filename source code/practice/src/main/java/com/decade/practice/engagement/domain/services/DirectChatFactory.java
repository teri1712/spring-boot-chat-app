package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatCreators;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DirectChatFactory implements ChatIdentifierMaker {
      @Override
      public String make(ChatCreators creators) {
            UUID smaller, bigger;
            if (creators.callerId().compareTo(creators.partnerId()) > 0) {
                  smaller = creators.partnerId();
                  bigger = creators.callerId();
            } else {
                  smaller = creators.callerId();
                  bigger = creators.partnerId();
            }
            return smaller + "+" + bigger;
      }

      public Chat create(@NotNull ChatCreators creators) {
            String chatId = make(creators);
            return new Chat(chatId, 2, creators);
      }
}
