package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.User;
import com.decade.practice.utils.ChatUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Conversation {
    private ChatDto chat;
    private UserResponse partner;
    private UserResponse owner;

    public Conversation(ChatDto chat, User partner, User owner) {
        this.chat = chat;
        this.partner = UserResponse.from(partner);
        this.owner = UserResponse.from(owner);
    }

    public Conversation(Chat chat, User owner) {
        this(
                new ChatDto(chat, owner),
                ChatUtils.inspectPartner(chat, owner),
                owner
        );
    }
}