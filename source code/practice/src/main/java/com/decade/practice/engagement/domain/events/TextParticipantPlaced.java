package com.decade.practice.engagement.domain.events;


import lombok.Getter;

import java.time.Instant;
import java.util.UUID;


@Getter
public class TextParticipantPlaced extends ParticipantPlaced {
    private final String content;

    public TextParticipantPlaced(UUID senderId, String chatId, UUID idempotencyKey, Instant createdAt, String content) {
        super(senderId, chatId, idempotencyKey, createdAt);
        this.content = content;
    }
}
