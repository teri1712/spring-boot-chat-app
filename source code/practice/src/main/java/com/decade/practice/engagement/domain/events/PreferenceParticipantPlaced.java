package com.decade.practice.engagement.domain.events;

import java.time.Instant;
import java.util.UUID;

public record PreferenceParticipantPlaced(UUID senderId,
                                          String chatId,
                                          UUID idempotencyKey,
                                          Instant createdAt,
                                          Integer iconId,
                                          String roomName,
                                          String roomAvatar,
                                          Long themeId) {
}
