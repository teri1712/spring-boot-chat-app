package com.decade.practice.persistence.jpa.embeddables;

import com.decade.practice.persistence.jpa.entities.Theme;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class Preference {

    @NotNull
    private Integer iconId = 1;

    @NotNull
    @NotBlank
    private String roomName;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    private Theme theme;

}
