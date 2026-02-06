package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;

import java.io.Serializable;
import java.util.UUID;

public record ChatResponse(ChatIdentifier identifier, UUID owner, UUID partner) implements Serializable {
    public static ChatResponse from(ChatIdentifier identifier, UUID owner) {
        UUID partner = identifier.getFirstUser().equals(owner) ?
                identifier.getSecondUser() : identifier.getFirstUser();
        return new ChatResponse(identifier, owner, partner);
    }

    public static ChatResponse from(Chat chat, User owner) {
        return from(chat.getIdentifier(), owner.getId());
    }
}
