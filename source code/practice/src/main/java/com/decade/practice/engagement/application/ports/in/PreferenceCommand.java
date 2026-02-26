package com.decade.practice.engagement.application.ports.in;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PreferenceCommand extends ParticipantCommand {
      private final Integer iconId;
      private final String roomName;
      private final String roomAvatar;
      private final Long themeId;

      public PreferenceCommand(String chatId, UUID senderId, UUID idempotentKey, Integer iconId, String roomName, String roomAvatar, Long themeId) {
            super(chatId, senderId, idempotentKey);
            this.iconId = iconId;
            this.roomName = roomName;
            this.roomAvatar = roomAvatar;
            this.themeId = themeId;
      }
}
