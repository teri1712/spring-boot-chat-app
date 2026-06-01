package com.decade.practice.live.domain.events;

import java.time.Instant;
import java.util.UUID;

public record JoinerTyped(String chatId, UUID userId, String avatar, Instant at) {
}
