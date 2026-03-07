package com.decade.practice.presence.application.ports.in;

import com.decade.practice.presence.dto.PresenceRecommendationResponse;

import java.time.Instant;
import java.util.UUID;

public interface PresenceSetter {
      PresenceRecommendationResponse set(UUID userId, String name, String avatar, Instant at);
}
