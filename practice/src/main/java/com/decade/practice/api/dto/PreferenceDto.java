package com.decade.practice.api.dto;

import com.decade.practice.persistence.jpa.entities.User;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PreferenceDto {
    private int resourceId;
    private String roomName;

    //TODO: check client
    private Integer themeId;

    public PreferenceDto(User firstUser, User secondUser) {
        this.resourceId = 1;
        this.roomName = "Room " + firstUser.getUsername() + " and " + secondUser.getUsername();
    }

    public PreferenceDto() {
    }

}
