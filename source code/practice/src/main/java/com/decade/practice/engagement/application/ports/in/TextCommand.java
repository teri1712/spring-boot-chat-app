package com.decade.practice.engagement.application.ports.in;


import lombok.Getter;

import java.util.UUID;


@Getter
public class TextCommand extends EventCommand {
    private final String content;

    public TextCommand(String chatId, UUID senderId, UUID idempotentKey, String content) {
        super(chatId, senderId, idempotentKey);
        this.content = content;
    }
}
