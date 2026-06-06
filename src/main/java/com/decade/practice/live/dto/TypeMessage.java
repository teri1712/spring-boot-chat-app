package com.decade.practice.live.dto;

import java.time.Instant;
import java.util.UUID;

public record TypeMessage(
          UUID from,
          String avatar,
          String chatId,
          Instant time
) {

}