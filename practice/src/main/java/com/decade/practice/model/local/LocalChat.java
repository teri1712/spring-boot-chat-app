package com.decade.practice.model.local;

import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;
import java.util.UUID;

@JsonDeserialize
public class LocalChat {
    private final ChatIdentifier identifier;
    private final UUID owner;
    private final UUID partner;

    public LocalChat(ChatIdentifier identifier, UUID owner) {
        this.identifier = identifier;
        this.owner = owner;
        this.partner = identifier.getFirstUser().equals(owner) ?
                identifier.getSecondUser() : identifier.getFirstUser();
    }

    public LocalChat(Chat chat, User owner) {
        this(chat.getIdentifier(), owner.getId());
    }

    public ChatIdentifier getIdentifier() {
        return identifier;
    }

    public UUID getOwner() {
        return owner;
    }

    public UUID getPartner() {
        return partner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalChat localChat = (LocalChat) o;
        return Objects.equals(identifier, localChat.identifier) &&
                Objects.equals(owner, localChat.owner) &&
                Objects.equals(partner, localChat.partner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, owner, partner);
    }

    @Override
    public String toString() {
        return "LocalChat{" +
                "identifier=" + identifier +
                ", owner=" + owner +
                ", partner=" + partner +
                '}';
    }
}