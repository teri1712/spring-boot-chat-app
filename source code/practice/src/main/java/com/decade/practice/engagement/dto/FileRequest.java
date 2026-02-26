package com.decade.practice.engagement.dto;

import jakarta.validation.constraints.NotNull;

public record FileRequest(

          @NotNull
          String filename,
          @NotNull
          Integer size,
          // TODO: Adjust client
          @NotNull
          String uri
) {


}
