package com.decade.practice.inbox.dto;

import com.decade.practice.resources.files.api.FileIntegrity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImageRequest(
          @NotNull
          FileIntegrity file,

          @NotBlank
          String filename,

          @Min(200)
          @Max(2000)
          Integer width,

          @Min(200)
          @Max(2000)
          Integer height,

          @Nullable
          String format

) {
}
