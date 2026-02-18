package com.decade.practice.engagement.api;

import java.util.UUID;

public record EngagementRule(
        UUID userId,
        String chatId,
        boolean write,
        boolean read) {
}
