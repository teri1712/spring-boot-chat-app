package com.decade.practice.chatsettings.dto;

import jakarta.annotation.Nullable;

public record PreferenceRequest(

          @Nullable
          Integer iconId,

          @Nullable
          String customName,

          @Nullable
          Long themeId,

          @Nullable
          // todo: fix client
          String customAvatar

) {
}
