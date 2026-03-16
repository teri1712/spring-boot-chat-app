package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatCreators;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DirectChatFactory implements IdempotentIdentifierMaker {
      @Override
      public String make(ChatCreators creators) {
            UUID smaller, bigger;
            UUID callerId = creators.callerId();
            UUID partnerId = creators.partners().stream().findFirst().orElse(callerId);

            if (creators.callerId().compareTo(partnerId) > 0) {
                  smaller = partnerId;
                  bigger = creators.callerId();
            } else {
                  smaller = creators.callerId();
                  bigger = partnerId;
            }
            return smaller + "+" + bigger;
      }

      public Chat create(@NotNull ChatCreators creators) {
            String chatId = make(creators);
            return new Chat(chatId, 2, creators);
      }
}
