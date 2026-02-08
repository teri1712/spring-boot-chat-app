package com.decade.practice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ImageRequest(
        @NotBlank
        String uri,

        @NotBlank
        String filename,

        @Min(200)
        @Max(2000)
        Integer width,

        @Min(200)
        @Max(2000)
        Integer height,

        @Nullable
        String format
) {
}
