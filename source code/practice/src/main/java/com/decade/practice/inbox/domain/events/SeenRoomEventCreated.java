package com.decade.practice.inbox.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public class SeenRoomEventCreated extends RoomEventCreated {
      private final Instant at;

}
