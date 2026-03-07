package com.decade.practice.engagement.domain.events;

import java.util.List;
import java.util.UUID;

public record ChatCreated(String chatId, List<UUID> participants, UUID callerId) {
}
