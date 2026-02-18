package com.decade.practice.engagement.api.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@SuperBuilder
public class SeenEventPlaced extends EventPlaced {
    private final Instant at;

}
