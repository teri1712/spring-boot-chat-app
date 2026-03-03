package com.decade.practice.engagement.domain.events;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
public class ChatCreatedAccepted {

      private final UUID senderId;
      private final String chatId;

      private final ChatSnapshot snapshot;

      private final Instant createdAt;
}
