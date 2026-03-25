package com.decade.practice.presence.dto;

import java.time.Instant;

public record RoomPresenceResponse(String chatId, Instant at) {
}
