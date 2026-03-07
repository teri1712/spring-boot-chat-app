package com.decade.practice.inbox.domain;

import com.decade.practice.inbox.domain.events.IconChatEventCreated;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("ICON")
@Getter
public class IconChatEvent extends ChatEvent {

      private Integer iconId;

      protected IconChatEvent() {
      }

      public IconChatEvent(UUID idempotentKey, String chatId, UUID senderId, Integer iconId) {
            super(idempotentKey, chatId, senderId);
            this.iconId = iconId;
      }


      @Override
      protected void onCreated() {
            registerEvent(IconChatEventCreated.builder()
                      .chatEventId(getId())
                      .chatId(getChatId())

                      .senderId(getSenderId())
                      .iconId(getIconId())
                      .build());
      }


}
