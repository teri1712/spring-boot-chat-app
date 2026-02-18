package com.decade.practice.threads.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// TODO: Adjust client to poly
@AllArgsConstructor
@Getter
public class EventResponse implements Serializable {
    private final UUID id;
    private final UUID senderId;
    private final String roomNameSnapshot;
    private final String roomAvatarSnapshot;
    private final UUID ownerId;
    private final Instant createdAt;
    private final String eventType;
    private final Integer eventVersion;
    private final String chatId;
}
