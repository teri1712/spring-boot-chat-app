package com.decade.practice.engagement.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatEventResponse(
          UUID id,
          UUID senderId,
          String chatId,
          Instant createdAt
) {

}
