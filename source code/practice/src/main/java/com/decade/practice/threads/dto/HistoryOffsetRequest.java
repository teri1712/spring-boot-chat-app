package com.decade.practice.threads.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record HistoryOffsetRequest(
        @NotNull
        String chatId,
        @NotNull
        @Min(0)
        Long hashValue
) {
}
