package com.decade.practice.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ImageEventCreateCommand extends EventCreateCommand {

    private final String downloadUrl;
    private final String filename;
    private final Integer width;
    private final Integer height;
    private final String format;

    public ImageEventCreateCommand(String chatId, UUID senderId, String downloadUrl, String filename, Integer width, Integer height, String format) {
        super(chatId, senderId);
        this.downloadUrl = downloadUrl;
        this.filename = filename;
        this.width = width;
        this.height = height;
        this.format = format;
    }
}
