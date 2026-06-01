package com.decade.practice.inbox.application.ports.in;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public abstract class ParticipantCommand {
      private final String chatId;
      private final UUID senderId;
      private final UUID postingId;
}
