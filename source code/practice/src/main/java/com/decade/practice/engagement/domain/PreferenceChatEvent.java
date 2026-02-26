package com.decade.practice.engagement.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

import java.util.UUID;

@Entity
@DiscriminatorValue("Preference")
@Getter
public class PreferenceChatEvent extends ChatEvent {

      private Integer iconId;
      private String roomName;
      private String roomAvatar;
      private String theme;

      public PreferenceChatEvent(UUID id, String chatId, UUID senderId, Integer iconId, String roomName, String roomAvatar, String theme) {
            super(id, chatId, senderId);
            this.iconId = iconId;
            this.roomName = roomName;
            this.roomAvatar = roomAvatar;
            this.theme = theme;
      }

      protected PreferenceChatEvent() {
      }


}
