package com.decade.practice.domain.embeddables;

import com.decade.practice.domain.entities.Theme;
import com.decade.practice.domain.entities.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

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

        public String getRoomName() {
                return roomName;
        }

        public void setRoomName(String roomName) {
                this.roomName = roomName;
        }

        public int getResourceId() {
                return resourceId;
        }

        public void setResourceId(int resourceId) {
                this.resourceId = resourceId;
        }

        public Theme getTheme() {
                return theme;
        }

        public void setTheme(Theme theme) {
                this.theme = theme;
        }
}
