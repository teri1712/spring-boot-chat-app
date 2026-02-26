package com.decade.practice.live.application.ports.in;

import java.util.UUID;

public interface QueueService {
      void subQueue(UUID userId);

      void unSubQueue(UUID userId);
}
