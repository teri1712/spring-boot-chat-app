package com.decade.practice.engagement.application.ports.in;

import lombok.Getter;

import java.util.UUID;

@Getter
public class IconCommand extends EventCommand {


    private final Integer iconId;

    public IconCommand(String chatId, UUID senderId, UUID idempotentKey, Integer iconId) {
        super(chatId, senderId, idempotentKey);
        this.iconId = iconId;
    }
}
