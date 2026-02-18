package com.decade.practice.threads.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class TextResponse extends EventResponse {
    private final String content;

    public TextResponse(UUID id, UUID senderId, String roomNameSnapshot, String roomAvatarSnapshot, UUID ownerId, Instant createdAt, String eventType, Integer eventVersion, String chatId, String content) {
        super(id, senderId, roomNameSnapshot, roomAvatarSnapshot, ownerId, createdAt, eventType, eventVersion, chatId);
        this.content = content;
    }
}
