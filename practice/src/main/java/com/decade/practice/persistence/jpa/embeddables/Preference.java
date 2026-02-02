package com.decade.practice.persistence.jpa.embeddables;

import com.decade.practice.persistence.jpa.entities.Theme;
import com.decade.practice.persistence.jpa.entities.User;
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
    private Integer iconId;

    @NotNull
    @NotBlank
    private String roomName;

    @ManyToOne(fetch = FetchType.LAZY)
    @Nullable
    private Theme theme;

    public Preference(User firstUser, User secondUser) {
        this.iconId = 1;
        this.roomName = "Room " + firstUser.getName() + " and " + secondUser.getName();
    }

    public Preference() {
    }

}
