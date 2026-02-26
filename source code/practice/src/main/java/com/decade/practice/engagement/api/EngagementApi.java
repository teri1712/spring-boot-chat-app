package com.decade.practice.engagement.api;

import java.util.UUID;

public interface EngagementApi {
    EngagementRule find(String chatId, UUID userId);
}
