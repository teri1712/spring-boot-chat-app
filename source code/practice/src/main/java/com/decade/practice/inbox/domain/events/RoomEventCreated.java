package com.decade.practice.inbox.domain.events;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

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

@Jacksonized
public class RoomEventCreated {

      private final UUID senderId;
      private final UUID chatEventId;

      private final String chatId;

      private final Instant createdAt;
}
