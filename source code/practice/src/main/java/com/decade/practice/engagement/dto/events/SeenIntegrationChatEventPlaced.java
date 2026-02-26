package com.decade.practice.engagement.dto.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public class SeenIntegrationChatEventPlaced extends IntegrationChatEventPlaced {
      private final Instant at;

}
