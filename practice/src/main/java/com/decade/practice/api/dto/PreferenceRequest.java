package com.decade.practice.api.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PreferenceRequest {

    @NotNull
    private Integer iconId;

    @NotNull
    @NotBlank
    private String roomName;

    @Nullable
    private Integer themeId;

}
