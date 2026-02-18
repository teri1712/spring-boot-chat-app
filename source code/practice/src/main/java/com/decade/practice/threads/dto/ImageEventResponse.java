package com.decade.practice.threads.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
public class ImageEventResponse extends EventResponse {

    private final ImageResponse image;

}
