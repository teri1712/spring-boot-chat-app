package com.decade.practice.inbox.application.ports.in;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class SeenCommand extends ParticipantCommand {
      private final Instant at;

      public SeenCommand(String chatId, UUID senderId, UUID idempotentKey, Instant at) {
            super(chatId, senderId, idempotentKey);
            this.at = at;
      }
}
