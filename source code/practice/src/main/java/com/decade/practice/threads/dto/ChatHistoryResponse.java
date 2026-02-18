package com.decade.practice.threads.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChatHistoryResponse(
        String identifier,
        String roomName,
        String roomAvatar,
        Long hashValue,
        List<MessageResponse> messages,
        List<UUID> seenBy,
        Instant modifiedAt
) {


}
