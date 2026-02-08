package com.decade.practice.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PreferenceCreateCommand extends EventCreateCommand {
    private final Integer iconId;
    private final String roomName;
    private final Integer themeId;

    public PreferenceCreateCommand(String chatId, UUID senderId, Integer iconId, String roomName, Integer themeId) {
        super(chatId, senderId);
        this.iconId = iconId;
        this.roomName = roomName;
        this.themeId = themeId;
    }
}
