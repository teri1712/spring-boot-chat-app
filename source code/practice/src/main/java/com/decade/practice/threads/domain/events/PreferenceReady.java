package com.decade.practice.threads.domain.events;

import java.util.UUID;

public record PreferenceReady(String chatId, UUID ownerId, String roomName, String roomAvatar) {
}
