package com.decade.practice.inbox.domain.events;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record RoomCreated(String chatId, UUID creator, Instant at, Set<UUID> representatives) {
}
