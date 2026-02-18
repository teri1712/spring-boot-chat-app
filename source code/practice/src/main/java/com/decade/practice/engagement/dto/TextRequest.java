package com.decade.practice.engagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TextRequest(
        @NotNull
        @NotBlank
        String content
) {

}
