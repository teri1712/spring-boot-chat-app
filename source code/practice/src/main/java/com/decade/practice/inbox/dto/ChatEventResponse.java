package com.decade.practice.inbox.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatEventResponse(
          UUID postingId,
          UUID senderId,
          String chatId,
          Instant createdAt
) {

}
