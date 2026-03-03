package com.decade.practice.engagement.domain.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public class SeenChatEventAccepted extends ChatEventAccepted {
      private final Instant at;

}
