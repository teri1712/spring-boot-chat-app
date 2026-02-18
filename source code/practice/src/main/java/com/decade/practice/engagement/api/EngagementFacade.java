package com.decade.practice.engagement.api;

import java.util.UUID;

public interface EngagementFacade {
    EngagementRule find(String chatId, UUID userId);
}
