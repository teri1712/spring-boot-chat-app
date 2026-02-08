package com.decade.practice.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class SeenEventCreateCommand extends EventCreateCommand {
    private final Instant at;


    public SeenEventCreateCommand(String chatId, UUID senderId, Instant at) {
        super(chatId, senderId);
        this.at = at;
    }
}
