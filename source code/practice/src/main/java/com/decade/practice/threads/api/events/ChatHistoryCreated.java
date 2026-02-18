package com.decade.practice.threads.api.events;

import java.util.UUID;

//@Externalized("threads.chat-history.created::#{#this.chatId}")
public record ChatHistoryCreated(String chatId, UUID ownerId, String roomName) {
}
