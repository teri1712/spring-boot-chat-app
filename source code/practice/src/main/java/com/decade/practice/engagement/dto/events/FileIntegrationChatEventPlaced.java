package com.decade.practice.engagement.dto.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FileIntegrationChatEventPlaced extends IntegrationChatEventPlaced {
      private final String uri;
      private final String filename;
      private final Integer size;

}
