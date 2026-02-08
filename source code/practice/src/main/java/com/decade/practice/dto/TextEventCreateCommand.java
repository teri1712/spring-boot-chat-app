package com.decade.practice.dto;


import lombok.Getter;

import java.util.UUID;


@Getter
public class TextEventCreateCommand extends EventCreateCommand {
    private final String content;

    public TextEventCreateCommand(String chatId, UUID senderId, String content) {
        super(chatId, senderId);
        this.content = content;
    }
}
