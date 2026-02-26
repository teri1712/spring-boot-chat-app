package com.decade.practice.engagement.domain.events;

import java.util.UUID;

public record ChatEventPlaced(UUID id, String chatId, UUID senderId) {
}
