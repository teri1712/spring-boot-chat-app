package com.decade.practice.live.domain.events;

import java.util.UUID;

public record JoinerJoined(String chatId, UUID userId) {
}
