package com.decade.practice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.Instant;

public record SeenEventCreateRequest(
        @PastOrPresent
        @NotNull
        Instant at
) {

}
