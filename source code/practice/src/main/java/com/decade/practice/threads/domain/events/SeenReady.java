package com.decade.practice.threads.domain.events;

import java.util.UUID;

public record SeenReady(UUID sender, String chatId, UUID ownerId) {
}
