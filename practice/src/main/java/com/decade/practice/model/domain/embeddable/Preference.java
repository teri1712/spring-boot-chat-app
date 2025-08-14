package com.decade.practice.model.domain.embeddable;

import com.decade.practice.model.domain.entity.Theme;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

@Embeddable
public class Preference {
        private int resourceId;
        private String roomName;

        @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
        private Theme theme;

        public Preference(int resourceId, Theme theme) {
                this.resourceId = resourceId;
                this.theme = theme;
        }

        public Preference() {
                this(0, null);
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
