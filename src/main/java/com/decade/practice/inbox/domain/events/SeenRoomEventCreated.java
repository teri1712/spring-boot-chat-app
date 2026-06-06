package com.decade.practice.inbox.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Getter
@SuperBuilder

@Jacksonized
public class SeenRoomEventCreated extends RoomEventCreated {
      private final Instant at;

}
