package com.decade.practice.inbox.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FileChatEventCreated extends ChatEventCreated {
      private final String uri;
      private final String filename;
      private final Integer size;

}
