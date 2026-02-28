package com.decade.practice.engagement.dto.events;

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
//@Externalized("engagement.currentState.placed::#{#this.ownerId}")
@SuperBuilder
public class IntegrationChatEventPlaced {

      private final UUID senderId;
      private final UUID chatEventId;

      private final IntegrationChatSnapshot snapshot;

      private final Instant createdAt;
}
