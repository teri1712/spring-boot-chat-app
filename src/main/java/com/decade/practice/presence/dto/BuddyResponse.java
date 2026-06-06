package com.decade.practice.presence.dto;

import java.time.Instant;
import java.util.UUID;

public record BuddyResponse(UUID userId, String name, String avatar, Instant at) {
}
