package com.decade.practice.inbox.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatEventResponse(
          UUID id,
          UUID senderId,
          String chatId,
          Instant createdAt
) {

}
