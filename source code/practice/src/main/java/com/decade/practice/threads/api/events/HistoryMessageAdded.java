package com.decade.practice.threads.api.events;

import java.time.Instant;
import java.util.UUID;

//@Externalized("threads.chat-history.message-added::#{#this.chatId}")
public record HistoryMessageAdded(
        String chatId,
        String roomName,
        UUID ownerId,
        String message,
        Instant createdAt
) {
}
