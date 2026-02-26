package com.decade.practice.engagement.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("SEEN")
@Getter
public class SeenChatEvent extends ChatEvent {

      private Instant at;

      public SeenChatEvent(UUID idempotentKey, String chatId, UUID senderId, Instant at) {
            super(idempotentKey, chatId, senderId);
            this.at = at;
      }

      protected SeenChatEvent() {
      }


}
