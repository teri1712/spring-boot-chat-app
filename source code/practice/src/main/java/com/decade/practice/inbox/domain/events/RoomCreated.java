package com.decade.practice.inbox.domain.events;

import java.util.Set;
import java.util.UUID;

public record RoomCreated(String chatId, Set<UUID> representatives) {
}
