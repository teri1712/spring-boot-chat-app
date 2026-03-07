package com.decade.practice.inbox.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class IconChatEventCreated extends ChatEventCreated {


      private final Integer iconId;

}
