package com.decade.practice.threads.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(UUID sendBy, String content, Instant createdAt) {
}
