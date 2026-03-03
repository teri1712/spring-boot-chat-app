package com.decade.practice.engagement.dto;

import java.time.Instant;

// TODO: Adjust client
public record ChatResponse(
          String identifier,
          Boolean freshOne,
          Instant lastActivity,
          PreferenceResponse preference) {
}
