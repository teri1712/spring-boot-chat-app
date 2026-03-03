package com.decade.practice.presence.dto;

import java.time.Instant;
import java.util.UUID;

public record PresenceResponse(
          UUID userId,
          Instant at,
          String name,
          String avatar) {
}
