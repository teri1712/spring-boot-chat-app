package com.decade.practice.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageEventDto {

    @NotNull
    private String downloadUrl;

    @NotNull
    private String filename;

    @NotNull
    private Integer width;

    @NotNull
    private Integer height;

    @NotNull
    private String format;

}