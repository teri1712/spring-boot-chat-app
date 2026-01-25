package com.decade.practice.api.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.embeddables.Preference;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ChatResponse {
    private ChatIdentifier identifier;
    private UUID owner;
    private UUID partner;
    private Preference preference;

    public ChatResponse(ChatIdentifier identifier, UUID owner, Preference preference) {
        this.identifier = identifier;
        this.owner = owner;
        this.partner = identifier.getFirstUser().equals(owner) ?
                identifier.getSecondUser() : identifier.getFirstUser();
        this.preference = preference;
    }

    public ChatResponse(Chat chat, User owner) {
        this(chat.getIdentifier(), owner.getId(), chat.getPreference());
    }

}