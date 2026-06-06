package com.decade.practice.inbox.dto;

import jakarta.validation.constraints.NotNull;

public record IconRequest(
          @NotNull
          int iconId) {
}
