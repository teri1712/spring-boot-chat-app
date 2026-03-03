package com.decade.practice.engagement.domain.events;

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
public class ChatEventAccepted {

      private final UUID senderId;
      private final UUID chatEventId;

      private final ChatSnapshot snapshot;

      private final Instant createdAt;
}
