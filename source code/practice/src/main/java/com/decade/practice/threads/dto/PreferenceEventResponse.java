package com.decade.practice.threads.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class PreferenceEventResponse extends EventResponse {
    private final Integer iconId;
    private final String roomName;
    private final String roomAvatar;
    private final String theme;

    public PreferenceEventResponse(UUID id, UUID senderId, String roomNameSnapshot, String roomAvatarSnapshot, UUID ownerId, Instant createdAt, String eventType, Integer eventVersion, String chatId, Integer iconId, String roomName, String roomAvatar, String theme) {
        super(id, senderId, roomNameSnapshot, roomAvatarSnapshot, ownerId, createdAt, eventType, eventVersion, chatId);
        this.iconId = iconId;
        this.roomName = roomName;
        this.roomAvatar = roomAvatar;
        this.theme = theme;
    }
}
