package com.decade.practice.engagement.domain.events;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class SeenParticipantPlaced extends ParticipantPlaced {
    private final Instant at;

    public SeenParticipantPlaced(UUID senderId, String chatId, UUID idempotencyKey, Instant createdAt, Instant at) {
        super(senderId, chatId, idempotencyKey, createdAt);
        this.at = at;
    }
}
