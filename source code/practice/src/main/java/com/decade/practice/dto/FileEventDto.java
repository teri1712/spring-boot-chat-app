package com.decade.practice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
