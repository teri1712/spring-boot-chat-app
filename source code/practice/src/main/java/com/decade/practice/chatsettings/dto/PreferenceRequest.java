package com.decade.practice.chatsettings.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record PreferenceRequest(

          @Nullable
          Integer iconId,

          @Nullable
          @NotBlank
          String roomName,

          @Nullable
          Long themeId,

          @Nullable
          // todo: fix client
          String roomAvatar

) {
}
