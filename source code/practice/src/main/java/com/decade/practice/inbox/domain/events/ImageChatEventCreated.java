package com.decade.practice.inbox.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ImageChatEventCreated extends ChatEventCreated {

      private final String uri;
      private final Integer width;
      private final Integer height;
      private final String filename;
      private final String format;

}
