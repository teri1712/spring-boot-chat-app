package com.decade.practice.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class FileEventCreateCommand extends EventCreateCommand {
    private final String filename;
    private final int size;
    private final String mediaUrl;


    public FileEventCreateCommand(String chatId, UUID senderId, String filename, int size, String mediaUrl) {
        super(chatId, senderId);
        this.filename = filename;
        this.size = size;
        this.mediaUrl = mediaUrl;
    }
}
