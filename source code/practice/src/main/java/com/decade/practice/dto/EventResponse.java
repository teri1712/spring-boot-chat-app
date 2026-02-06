package com.decade.practice.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
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
) {
}
