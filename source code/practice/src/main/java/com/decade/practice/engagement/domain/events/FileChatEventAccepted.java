package com.decade.practice.engagement.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FileChatEventAccepted extends ChatEventAccepted {
      private final String uri;
      private final String filename;
      private final Integer size;

}
