package com.decade.practice.inbox.domain.events;

import com.decade.practice.inbox.domain.MessageState;

import java.time.Instant;
import java.util.UUID;

public record MessageCreated(
          Long id,
          UUID postingId,
          UUID senderId,
          String chatId,
          Instant createdAt,
          String messageType,
          MessageState currentState) {
}
