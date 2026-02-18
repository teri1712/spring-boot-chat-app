package com.decade.practice.engagement.domain.events;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ImageParticipantPlaced extends ParticipantPlaced {

    private final String uri;
    private final Integer width;
    private final Integer height;
    private final String filename;
    private final String format;

    public ImageParticipantPlaced(UUID senderId, String chatId, UUID idempotencyKey, Instant createdAt, String uri, Integer width, Integer height, String filename, String format) {
        super(senderId, chatId, idempotencyKey, createdAt);
        this.uri = uri;
        this.width = width;
        this.height = height;
        this.filename = filename;
        this.format = format;
    }
}
