package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.SeenRoomEventCreated;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("SEEN")
@Getter
public class SeenRoomEvent extends RoomEvent {

      private Instant at;

      public SeenRoomEvent(UUID idempotentKey, String chatId, UUID senderId, Instant at) {
            super(idempotentKey, chatId, senderId);
            this.at = at;
      }

      protected SeenRoomEvent() {
      }


      @Override
      protected void onCreated() {
            registerEvent(SeenRoomEventCreated.builder()
                      .chatEventId(getPostingId())
                      .chatId(getChatId())

                      .senderId(getSenderId())
                      .at(getAt())
                      .build());
      }

}
