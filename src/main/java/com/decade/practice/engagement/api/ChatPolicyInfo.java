package com.decade.practice.engagement.api;

import java.util.Set;
import java.util.UUID;

public record ChatPolicyInfo(
          String identifier,
          Integer maxParticipants,
          Set<UUID> creators) {
}
