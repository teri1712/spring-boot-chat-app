package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatCreators;
import com.decade.practice.persistence.redis.TypeEvent;

import java.time.Instant;
import java.util.UUID;

public record TypeEventResponse(
        UUID from,
        ChatCreators chat,
        Instant time,
        String key
) {
    public static TypeEventResponse from(TypeEvent event) {
        return new TypeEventResponse(
                event.getFrom(),
                event.getChat(),
                Instant.now(),
                event.getKey()
        );
    }
}
