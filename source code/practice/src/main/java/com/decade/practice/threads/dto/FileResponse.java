package com.decade.practice.threads.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FileResponse extends EventResponse {

    private final String filename;
    private final Integer size;
    private final String uri;

}
