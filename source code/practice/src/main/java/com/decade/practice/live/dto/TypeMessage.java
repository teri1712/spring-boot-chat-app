package com.decade.practice.live.dto;

import com.decade.practice.live.domain.LiveChatId;

import java.time.Instant;
import java.util.UUID;

public record TypeMessage(
        UUID from,
        LiveChatId chatId,
        Instant time
) {

}