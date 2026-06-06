package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.IconRoomEventCreated;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Entity
@DiscriminatorValue("ICON")
@Getter
public class IconRoomEvent extends RoomEvent {

      private Integer iconId;

      protected IconRoomEvent() {
      }

      public IconRoomEvent(UUID idempotentKey, String chatId, UUID senderId, Integer iconId) {
            super(idempotentKey, chatId, senderId);
            this.iconId = iconId;

      }


      @Override
      protected void onCreated() {
            registerEvent(IconRoomEventCreated.builder()
                      .chatEventId(getPostingId())
                      .chatId(getChatId())

                      .senderId(getSenderId())
                      .iconId(getIconId())
                      .build());
      }


}
