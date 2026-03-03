package com.decade.practice.engagement.domain.events;

import java.util.List;
import java.util.UUID;

public record ChatSnapshot(String chatId, String roomName, String roomAvatar, List<UUID> creators, List<UUID> participants) {
}
