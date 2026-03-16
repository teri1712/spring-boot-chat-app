package com.decade.practice.inbox.application.ports.in;

import lombok.Getter;

import java.util.UUID;

@Getter
public class IconCommand extends ParticipantCommand {


      private final Integer iconId;

      public IconCommand(String chatId, UUID senderId, UUID postingId, Integer iconId) {
            super(chatId, senderId, postingId);
            this.iconId = iconId;
      }
}
