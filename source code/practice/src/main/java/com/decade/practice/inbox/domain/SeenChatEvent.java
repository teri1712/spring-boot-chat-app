package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.SeenChatEventCreated;
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


      @Override
      protected void onCreated() {
            registerEvent(SeenChatEventCreated.builder()
                      .chatEventId(getId())
                      .chatId(getChatId())

                      .senderId(getSenderId())
                      .at(getAt())
                      .build());
      }

}
