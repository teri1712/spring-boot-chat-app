package com.decade.practice.threads.domain.events;

import java.time.Instant;
import java.util.UUID;

public record EventReady(
        UUID id,
        UUID senderId,
        String eventType,
        UUID ownerId,
        String chatId,
        Instant createdAt,
        Integer eventVersion) {
}
