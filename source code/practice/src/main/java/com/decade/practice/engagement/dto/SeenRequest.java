package com.decade.practice.engagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record SeenRequest(
        @PastOrPresent
        @NotNull
        Instant at
) {

}
