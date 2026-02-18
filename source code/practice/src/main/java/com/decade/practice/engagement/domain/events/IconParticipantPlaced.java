package com.decade.practice.engagement.domain.events;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class IconParticipantPlaced extends ParticipantPlaced {


    private final Integer iconId;

    public IconParticipantPlaced(UUID senderId, String chatId, UUID idempotencyKey, Instant createdAt, Integer iconId) {
        super(senderId, chatId, idempotencyKey, createdAt);
        this.iconId = iconId;
    }
}
