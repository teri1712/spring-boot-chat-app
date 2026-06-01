package com.decade.practice.engagement.domain.events;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ParticipantAdded(String chatId, Set<UUID> participantIds, Instant joinedAt) {
}
