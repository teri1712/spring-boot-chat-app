package com.decade.practice.api.dto;

import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.utils.ChatUtils;
import lombok.Data;

@Data
public class Conversation {
    private ChatResponse chat;
    private UserResponse partner;
    private UserResponse owner;

    public Conversation(ChatResponse chat, User partner, User owner) {
        this.chat = chat;
        this.partner = UserResponse.from(partner);
        this.owner = UserResponse.from(owner);
    }

    public Conversation() {
    }

    public Conversation(Chat chat, User owner) {
        this(
                new ChatResponse(chat, owner),
                ChatUtils.inspectPartner(chat, owner),
                owner
        );
    }
}