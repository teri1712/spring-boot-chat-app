package com.decade.practice.dto;

import jakarta.validation.constraints.NotNull;

public record FileEventCreateRequest(

        @NotNull
        String filename,
        @NotNull
        int size,
        @NotNull
        String mediaUrl
) {


}
