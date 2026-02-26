package com.decade.practice.engagement.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("TEXT")
@Getter
public class TextChatEvent extends ChatEvent {

      private String content;

      public TextChatEvent(UUID idempotentKey, String chatId, UUID senderId, String content) {
            super(idempotentKey, chatId, senderId);
            this.content = content;
      }

      protected TextChatEvent() {
      }


}
