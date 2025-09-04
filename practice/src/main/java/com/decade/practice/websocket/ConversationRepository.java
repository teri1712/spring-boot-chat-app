package com.decade.practice.websocket;

import com.decade.practice.models.domain.TypeEvent;
import com.decade.practice.models.domain.embeddable.ChatIdentifier;
import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.models.domain.entity.User;

import java.util.UUID;

public interface ConversationRepository {

        User getUser(String username);

        Chat getChat(ChatIdentifier id);

        TypeEvent getType(ChatIdentifier chat, UUID from, boolean readOnly);

        // Default method to replace Kotlin extension function
        default TypeEvent getType(Chat chat, User from, boolean readOnly) {
                return getType(chat.getIdentifier(), from.getId(), readOnly);
        }
}