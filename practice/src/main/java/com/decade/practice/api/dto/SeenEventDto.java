package com.decade.practice.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeenEventDto {

    @PastOrPresent
    @NotNull
    // TODO: Adjust client
    private Instant at;
}