package com.decade.practice.inbox.apis.events;

import java.util.UUID;

//@Externalized("threads.chat-history.created::#{#this.chatId}")
public record ConversationCreated(String chatId, UUID ownerId, String roomName) {
}
