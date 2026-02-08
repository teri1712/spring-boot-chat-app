package com.decade.practice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TextEventRequest(
        @NotNull
        @NotBlank
        String content
) {

}
