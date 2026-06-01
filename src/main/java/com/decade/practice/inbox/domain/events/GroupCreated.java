package com.decade.practice.inbox.domain.events;

import java.util.UUID;

public record GroupCreated(String chatId, UUID creator) {
}
