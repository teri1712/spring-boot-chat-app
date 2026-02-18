package com.decade.practice.threads.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public class SeenResponse extends EventResponse {
    private final Instant at;

}
