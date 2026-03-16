package com.decade.practice.engagement.api;

import java.util.Set;
import java.util.UUID;

// TODO: Adjust client
public record ChatPolicyInfo(
          String identifier,
          Integer maxParticipants,
          Set<UUID> creators) {
}
