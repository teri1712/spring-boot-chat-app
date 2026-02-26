package com.decade.practice.inbox.apis.events;

import java.time.Instant;
import java.util.UUID;

//@Externalized("threads.chat-history.currentState-added::#{#this.chatId}")
public record MessageAdded(
          String chatId,
          String roomName,
          UUID ownerId,
          String message,
          Instant createdAt
) {
}
