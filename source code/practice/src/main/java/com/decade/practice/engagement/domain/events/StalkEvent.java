package com.decade.practice.engagement.domain.events;

import java.util.UUID;

public record StalkEvent(UUID senderId, UUID receiverId) {
}
