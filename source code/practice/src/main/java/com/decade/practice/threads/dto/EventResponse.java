package com.decade.practice.threads.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// TODO: Adjust client to poly
@SuperBuilder
@Getter
public class EventResponse implements Serializable {
    private final UUID id;
    private final UUID senderId;
    private final String roomNameSnapshot;
    private final String roomAvatarSnapshot;
    private final Long roomHashSnapshot;
    private final UUID ownerId;
    private final Instant createdAt;
    private final String eventType;
    private final Integer eventVersion;
    private final String chatId;
}
