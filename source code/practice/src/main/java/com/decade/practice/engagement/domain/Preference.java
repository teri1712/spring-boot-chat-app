package com.decade.practice.engagement.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

@Embeddable
public record Preference(

        @NotNull
        Integer iconId,

        @Nullable
        String roomName,

        @Nullable
        // TODO: Adjust client
        String roomAvatar,

        @Nullable
        String theme

) {

}
