package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.utils.ChatUtils;

public record Conversation(
        ChatDto chat,
        UserResponse partner,
        UserResponse owner

) {
    public Conversation(ChatDto chat, User partner, User owner) {
        this(chat, UserResponse.from(partner), UserResponse.from(owner));
    }

    public Conversation(Chat chat, User owner) {
        this(
                new ChatDto(chat, owner),
                ChatUtils.inspectPartner(chat, owner),
                owner
        );
    }
}