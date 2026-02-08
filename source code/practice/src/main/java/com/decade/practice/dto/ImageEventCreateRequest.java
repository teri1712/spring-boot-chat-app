package com.decade.practice.dto;

import jakarta.validation.constraints.NotNull;

public record ImageEventCreateRequest(

        @NotNull
        String downloadUrl,
        @NotNull
        String filename,
        @NotNull
        Integer width,
        @NotNull
        Integer height,
        @NotNull
        String format

) {
}
