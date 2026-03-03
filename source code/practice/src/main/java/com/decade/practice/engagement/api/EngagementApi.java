package com.decade.practice.engagement.api;

import java.util.UUID;

public interface EngagementApi {
      boolean canRead(String chatId, UUID userId);

      boolean canWrite(String chatId, UUID userId);
}
