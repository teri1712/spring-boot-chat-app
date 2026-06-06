package com.decade.practice.web.events;

import java.time.Instant;
import java.util.UUID;

public record ConnectionInteracted(UUID userId, String ipAddress, Instant at, String agent) {
}
