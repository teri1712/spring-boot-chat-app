package com.decade.practice.api.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ChatDto {
    private ChatIdentifier identifier;
    private UUID owner;
    private UUID partner;

    public ChatDto(ChatIdentifier identifier, UUID owner) {
        this.identifier = identifier;
        this.owner = owner;
        this.partner = identifier.getFirstUser().equals(owner) ?
                identifier.getSecondUser() : identifier.getFirstUser();
    }

    public ChatDto(Chat chat, User owner) {
        this(chat.getIdentifier(), owner.getId());
    }

}