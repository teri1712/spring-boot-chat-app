package com.decade.practice.engagement.domain.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class ParticipantPlaced {

    private final UUID senderId;
    private final String chatId;
    private final UUID idempotencyKey;
    private final Instant createdAt;

}
