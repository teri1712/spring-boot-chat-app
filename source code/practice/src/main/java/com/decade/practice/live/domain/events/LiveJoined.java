package com.decade.practice.live.domain.events;

import java.util.UUID;

public record LiveJoined(String liveChatId, UUID userId) {
}
