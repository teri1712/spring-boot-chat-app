package com.decade.practice.engagement.api;

import java.util.List;
import java.util.UUID;

// TODO: Adjust client
public record ChatPolicyInfo(
          String identifier,
          Integer maxParticipants,
          List<UUID> creators) {
}
