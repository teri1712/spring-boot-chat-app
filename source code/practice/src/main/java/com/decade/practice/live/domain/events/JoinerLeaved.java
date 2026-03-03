package com.decade.practice.live.domain.events;

import java.util.UUID;

public record JoinerLeaved(String chatId, UUID userId) {
}
