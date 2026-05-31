package com.decade.practice.inbox.domain.events;

import java.time.Instant;
import java.util.UUID;

public record TextAdded(Long sequenceNumber,
                        String text,
                        String chatId,
                        Instant createdAt,
                        UUID postingId,
                        UUID senderId) {
}
