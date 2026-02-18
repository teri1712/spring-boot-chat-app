package com.decade.practice.threads.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class TextResponse extends EventResponse {
    private final String content;

}
