package com.decade.practice.threads.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class FileResponse extends EventResponse {

    private final String filename;
    private final Integer size;
    private final String uri;

    public FileResponse(UUID id, UUID senderId, String roomNameSnapshot, String roomAvatarSnapshot, UUID ownerId, Instant createdAt, String eventType, Integer eventVersion, String chatId, String filename, Integer size, String uri) {
        super(id, senderId, roomNameSnapshot, roomAvatarSnapshot, ownerId, createdAt, eventType, eventVersion, chatId);
        this.filename = filename;
        this.size = size;
        this.uri = uri;
    }
}
