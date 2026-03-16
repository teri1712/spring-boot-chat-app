package com.decade.practice.engagement.domain.events;

import java.util.Set;
import java.util.UUID;

public record ChatCreated(String chatId, Set<UUID> participants, UUID callerId) {
}
