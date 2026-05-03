package com.decade.practice.inbox.dto;

import com.decade.practice.resources.files.api.FileIntegrity;
import jakarta.validation.constraints.NotNull;

public record FileRequest(

          @NotNull
          String filename,
          @NotNull
          Integer size,
          @NotNull
          FileIntegrity file
) {


}
