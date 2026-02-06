package com.decade.practice.dto;

import java.io.Serializable;
import java.time.Instant;

public record SeenEventResponse(Instant at) implements Serializable {
}
