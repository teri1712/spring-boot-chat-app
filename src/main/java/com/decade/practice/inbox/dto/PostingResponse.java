package com.decade.practice.inbox.dto;

import java.time.Instant;
import java.util.UUID;

public record PostingResponse(
          UUID postingId,
          UUID senderId,
          String chatId,
          Instant createdAt
) {

}
