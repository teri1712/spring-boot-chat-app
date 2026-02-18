package com.decade.practice.engagement.api.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FileEventPlaced extends EventPlaced {
    private final String uri;
    private final String filename;
    private final Integer size;

}
