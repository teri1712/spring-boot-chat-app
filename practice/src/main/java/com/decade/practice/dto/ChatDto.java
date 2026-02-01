package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
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