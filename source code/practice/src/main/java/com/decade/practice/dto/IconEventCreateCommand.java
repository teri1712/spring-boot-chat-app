package com.decade.practice.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class IconEventCreateCommand extends EventCreateCommand {
    private final int iconId;


    public IconEventCreateCommand(String chatId, UUID senderId, int iconId) {
        super(chatId, senderId);
        this.iconId = iconId;
    }
}
