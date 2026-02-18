package com.decade.practice.engagement.domain.events;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ProcessedPreferenceParticipantPlaced extends ParticipantPlaced {

    private final Integer iconId;
    private final String roomName;
    private final String roomAvatar;
    private final Long themeId;
    private final String theme;

    public ProcessedPreferenceParticipantPlaced(UUID senderId, String chatId, UUID idempotencyKey, Instant createdAt, Integer iconId, String roomName, String roomAvatar, Long themeId, String theme) {
        super(senderId, chatId, idempotencyKey, createdAt);
        this.iconId = iconId;
        this.roomName = roomName;
        this.roomAvatar = roomAvatar;
        this.themeId = themeId;
        this.theme = theme;
    }
}
