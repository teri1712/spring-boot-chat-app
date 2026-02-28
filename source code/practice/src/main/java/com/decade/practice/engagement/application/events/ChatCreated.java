package com.decade.practice.engagement.application.events;

import java.util.List;
import java.util.UUID;

public record ChatCreated(String chatId, String roomName, String roomAvatar, List<UUID> participants, List<UUID> creators, UUID callerId) {
}
