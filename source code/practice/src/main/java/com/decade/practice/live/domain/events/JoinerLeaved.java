package com.decade.practice.live.domain.events;

import com.decade.practice.live.domain.LiveChatId;

import java.util.UUID;

public record JoinerLeaved(LiveChatId chatId, UUID userId) {
}
