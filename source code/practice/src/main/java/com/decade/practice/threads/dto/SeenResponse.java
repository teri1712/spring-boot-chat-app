package com.decade.practice.threads.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class SeenResponse extends EventResponse {
    private final Instant at;

    public SeenResponse(UUID id, UUID senderId, String roomNameSnapshot, String roomAvatarSnapshot, UUID ownerId, Instant createdAt, String eventType, Integer eventVersion, String chatId, Instant at) {
        super(id, senderId, roomNameSnapshot, roomAvatarSnapshot, ownerId, createdAt, eventType, eventVersion, chatId);
        this.at = at;
    }
}
