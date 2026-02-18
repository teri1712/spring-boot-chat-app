package com.decade.practice.threads.domain.events;

import java.time.Instant;
import java.util.UUID;

public record MessageReady(UUID id, String message, Instant createdAt, UUID ownerId, String chatId, UUID senderId) {
}
