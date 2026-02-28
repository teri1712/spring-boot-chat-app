package com.decade.practice.engagement.dto.events;

import java.util.List;
import java.util.UUID;

public record IntegrationChatSnapshot(String chatId, String roomName, String roomAvatar, List<UUID> creators, List<UUID> participants) {
}
