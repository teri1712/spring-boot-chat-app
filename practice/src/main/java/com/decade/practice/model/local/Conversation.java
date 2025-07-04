package com.decade.practice.model.local;

import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.utils.ChatUtils;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

@JsonDeserialize
public class Conversation {
    private final LocalChat chat;
    private final User partner;
    private final User owner;

    public Conversation(LocalChat chat, User partner, User owner) {
        this.chat = chat;
        this.partner = partner;
        this.owner = owner;
    }

    public Conversation(Chat chat, User owner) {
        this(
            new LocalChat(chat, owner),
            ChatUtils.inspectPartner(chat, owner),
            owner
        );
    }

    public LocalChat getChat() {
        return chat;
    }

    public User getPartner() {
        return partner;
    }

    public User getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(chat, that.chat) &&
                Objects.equals(partner, that.partner) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chat, partner, owner);
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "chat=" + chat +
                ", partner=" + partner +
                ", owner=" + owner +
                '}';
    }
}