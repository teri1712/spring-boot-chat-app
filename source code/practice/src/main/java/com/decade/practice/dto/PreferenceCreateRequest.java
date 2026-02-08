package com.decade.practice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PreferenceCreateRequest(
        @NotNull
        Integer iconId,

        @NotNull
        @NotBlank
        String roomName,

        @Nullable
        Integer themeId

) {
}
