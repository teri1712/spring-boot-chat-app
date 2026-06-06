package com.decade.practice.chatorchestrator.dto;

import java.util.Set;
import java.util.UUID;

public record ChatResponse(
          String identifier,
          Integer maxParticipants,
          Set<UUID> creators,
          PreferenceResponse preference) {
}
