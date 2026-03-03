package com.decade.practice.engagement.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class IconChatEventAccepted extends ChatEventAccepted {


      private final Integer iconId;

}
