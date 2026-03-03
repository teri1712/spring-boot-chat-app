package com.decade.practice.presence.application.ports.in;

import com.decade.practice.presence.dto.PresenceResponse;

import java.time.Instant;
import java.util.UUID;

public interface PresenceSetter {
      PresenceResponse set(UUID userId, String name, String avatar, Instant at);
}
