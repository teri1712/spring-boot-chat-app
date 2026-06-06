package com.decade.practice.inbox.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder

@Jacksonized
public class ImageRoomEventCreated extends RoomEventCreated {

      private final String uri;
      private final Integer width;
      private final Integer height;
      private final String filename;
      private final String format;

}
