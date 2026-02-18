package com.decade.practice.engagement.api.events;

import java.util.List;
import java.util.UUID;

public record ChatCreated(String roomName, String roomAvatar, String chatId, List<UUID> participants) {
}
