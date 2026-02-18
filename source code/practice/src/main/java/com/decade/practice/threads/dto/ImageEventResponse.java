package com.decade.practice.threads.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;


@Getter
public class ImageEventResponse extends EventResponse {

    private final ImageResponse image;

    public ImageEventResponse(UUID id, UUID senderId, String roomNameSnapshot, String roomAvatarSnapshot, UUID ownerId, Instant createdAt, String eventType, Integer eventVersion, String chatId, ImageResponse image) {
        super(id, senderId, roomNameSnapshot, roomAvatarSnapshot, ownerId, createdAt, eventType, eventVersion, chatId);
        this.image = image;
    }
}
