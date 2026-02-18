package com.decade.practice.engagement.application.ports.in;

import lombok.Getter;

import java.util.UUID;

@Getter
public class FileCommand extends EventCommand {
    private final String uri;
    private final String filename;
    private final Integer size;

    public FileCommand(String chatId, UUID senderId, UUID idempotentKey, String uri, String filename, Integer size) {
        super(chatId, senderId, idempotentKey);
        this.uri = uri;
        this.filename = filename;
        this.size = size;
    }
}
