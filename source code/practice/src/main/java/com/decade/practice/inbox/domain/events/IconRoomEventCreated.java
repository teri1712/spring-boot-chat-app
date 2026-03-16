package com.decade.practice.inbox.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class IconRoomEventCreated extends RoomEventCreated {


      private final Integer iconId;

}
