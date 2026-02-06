package com.decade.practice.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// TODO: Adjust client
public record EventResponse(
        UUID id,
        UUID idempotencyKey,
        UUID sender,
        TextEventResponse textEvent,
        ImageEventResponse imageEvent,
        IconEventResponse iconEvent,
        PreferenceEventResponse preferenceEvent,
        FileEventResponse fileEvent,
        SeenEventResponse seenEvent,
        Instant createdTime,
        String eventType,
        Integer eventVersion,
        boolean message,
        UUID owner,
        UUID partner,
        ChatResponse chat
) implements Serializable {
}
