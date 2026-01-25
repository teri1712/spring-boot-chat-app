package com.decade.practice.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEventDto {

    @NotNull
    private String filename;

    @NotNull
    private int size;
    @NotNull
    private String mediaUrl;

}
