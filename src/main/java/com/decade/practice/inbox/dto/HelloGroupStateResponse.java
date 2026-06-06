package com.decade.practice.inbox.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Getter
@SuperBuilder

@Jacksonized
public class HelloGroupStateResponse extends MessageStateResponse {
    private final UUID creator;

}
