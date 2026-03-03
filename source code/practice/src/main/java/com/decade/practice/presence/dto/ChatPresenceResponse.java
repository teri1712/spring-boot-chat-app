package com.decade.practice.presence.dto;

import java.time.Instant;

public record ChatPresenceResponse(String chatId, Instant at) {
}
