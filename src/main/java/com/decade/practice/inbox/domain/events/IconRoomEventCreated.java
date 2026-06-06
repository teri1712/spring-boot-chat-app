package com.decade.practice.inbox.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder

@Jacksonized
public class IconRoomEventCreated extends RoomEventCreated {


      private final Integer iconId;

}
