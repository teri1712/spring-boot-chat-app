package com.decade.practice.application.dto;

import com.decade.practice.domain.embeddables.ImageSpec;
import com.decade.practice.domain.entities.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class Profile implements Serializable {

        private String username;

        @Size(min = 4, max = 100, message = "Name length must be between 4-100")
        private String name;
        private ImageSpec avatar;

        @NotEmpty
        private String gender;

        @Past(message = "Do you have time machine.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date birthday;
        private boolean allowToUpdate = true;

        public Profile(User user) {
                this.name = user.getName();
                this.avatar = user.getAvatar();
                this.gender = user.getGender().stream().findFirst().orElse(null);
                this.birthday = user.getDob();
                this.username = user.getUsername();
        }

        protected Profile() {
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public boolean isAllowToUpdate() {
                return allowToUpdate;
        }

        public void setAllowToUpdate(boolean allowToUpdate) {
                this.allowToUpdate = allowToUpdate;
        }

        public String getName() {
                return name;
        }

        public String getUsername() {
                return username;
        }

        public void setName(String name) {
                this.name = name;
        }

        public ImageSpec getAvatar() {
                return avatar;
        }

        public void setAvatar(ImageSpec avatar) {
                this.avatar = avatar;
        }

        public String getGender() {
                return gender;
        }

        public void setGender(String gender) {
                this.gender = gender;
        }

        public Date getBirthday() {
                return birthday;
        }

        public void setBirthday(Date birthday) {
                this.birthday = birthday;
        }
}
