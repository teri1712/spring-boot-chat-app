package com.decade.practice.dto;

import jakarta.validation.constraints.NotNull;

public record IconEventCreateRequest(
        @NotNull
        int iconId) {
}
