package com.decade.practice.engagement.dto;

import jakarta.validation.constraints.NotNull;

public record IconRequest(
        @NotNull
        int iconId) {
}
