package com.decade.practice.presence.dto;

import java.time.Instant;
import java.util.UUID;

public record PresenceRecommendationResponse(
          UUID userId,
          Instant at,
          String name,
          String avatar) {
}
