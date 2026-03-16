package com.decade.practice.inbox.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FileRoomEventCreated extends RoomEventCreated {

      private final String uri;
      private final String filename;
      private final Integer size;

}
