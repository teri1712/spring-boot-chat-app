package com.decade.practice.engagement.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ImageChatEventAccepted extends ChatEventAccepted {

      private final String uri;
      private final Integer width;
      private final Integer height;
      private final String filename;
      private final String format;

}
