package com.decade.practice.engagement.dto;

import java.time.Instant;
import java.util.UUID;

public record ReceiptResponse(
        UUID idempotentKey,
        UUID senderId,
        String chatId,
        Instant createdAt
) {

}
