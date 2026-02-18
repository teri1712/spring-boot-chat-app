package com.decade.practice.engagement.api.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Getter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
//@Externalized("engagement.event.placed::#{#this.ownerId}")
@SuperBuilder
public class EventPlaced {

    private final UUID senderId;

    private final ChatSnapshot snapshot;

    private final Instant createdAt;
}
