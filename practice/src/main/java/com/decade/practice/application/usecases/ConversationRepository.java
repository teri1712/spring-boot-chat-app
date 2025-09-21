package com.decade.practice.application.usecases;

import com.decade.practice.domain.TypeEvent;
import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.User;

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