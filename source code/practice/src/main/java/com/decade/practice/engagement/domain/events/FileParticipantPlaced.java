package com.decade.practice.engagement.domain.events;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class FileParticipantPlaced extends ParticipantPlaced {


    private final String uri;
    private final String filename;
    private final Integer size;

    public FileParticipantPlaced(UUID senderId, String chatId, UUID idempotencyKey, Instant createdAt, String uri, String filename, Integer size) {
        super(senderId, chatId, idempotencyKey, createdAt);
        this.uri = uri;
        this.filename = filename;
        this.size = size;
    }
}
