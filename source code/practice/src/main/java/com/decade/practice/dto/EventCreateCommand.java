package com.decade.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class EventCreateCommand {

    private final String chatId;
    private final UUID senderId;

}
