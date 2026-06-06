package com.decade.practice.inbox.application.ports.in;


import lombok.Getter;

import java.util.UUID;


@Getter
public class TextCommand extends ParticipantCommand {
      private final String content;

      public TextCommand(String chatId, UUID senderId, UUID postingId, String content) {
            super(chatId, senderId, postingId);
            this.content = content;
      }
}
