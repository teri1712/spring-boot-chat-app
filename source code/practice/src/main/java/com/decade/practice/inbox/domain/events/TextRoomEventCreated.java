package com.decade.practice.inbox.domain.events;


import lombok.Getter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder

public class TextRoomEventCreated extends RoomEventCreated {

      private final String content;

}
