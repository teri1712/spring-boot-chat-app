package com.decade.practice.persistence.jpa.embeddables;

import com.decade.practice.persistence.jpa.entities.Theme;
import com.decade.practice.persistence.jpa.entities.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class Preference {
    private int resourceId;
    private String roomName;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    private Theme theme;

    public Preference(User firstUser, User secondUser) {
        this.resourceId = 1;
        this.roomName = "Room " + firstUser.getUsername() + " and " + secondUser.getUsername();
    }

    public Preference() {
    }

}
