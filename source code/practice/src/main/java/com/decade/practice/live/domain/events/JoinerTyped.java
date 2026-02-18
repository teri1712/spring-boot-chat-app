package com.decade.practice.live.domain.events;

import com.decade.practice.live.domain.LiveChatId;

import java.time.Instant;
import java.util.UUID;

public record JoinerTyped(LiveChatId chatId, UUID userId, Instant at) {
}
