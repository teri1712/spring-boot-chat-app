package com.decade.practice.engagement.api.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ImageEventPlaced extends EventPlaced {

    private final String uri;
    private final Integer width;
    private final Integer height;
    private final String filename;
    private final String format;

}
