package com.decade.practice.chatsettings.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Embeddable
public record Preference(

          @NotNull
          Integer iconId,

          @Nullable
          String customName,

          @Nullable
          // TODO: Adjust client
          String customAvatar,

          @ManyToOne
          Theme theme

) {

}
