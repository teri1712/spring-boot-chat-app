package com.decade.practice.inbox.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TextRequest(
          @NotNull
          @NotBlank
          String content
) {

}
