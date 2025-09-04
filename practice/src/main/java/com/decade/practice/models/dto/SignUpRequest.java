package com.decade.practice.models.dto;

import com.decade.practice.web.validation.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

public class SignUpRequest {
        // Constants moved from AuthenticationController.kt
        public static final int MAX_USERNAME_LENGTH = 20;
        public static final int MIN_USERNAME_LENGTH = 5;

        @Size(
                min = MIN_USERNAME_LENGTH,
                max = MAX_USERNAME_LENGTH,
                message = "Username length must be between "
                        + MIN_USERNAME_LENGTH + " and " + MAX_USERNAME_LENGTH
                        + " characters"
        )
        @NotBlank(message = "Username must not be empty")
        @Pattern(regexp = "\\S+", message = "Username must not contain spaces.")
        private String username;

        @StrongPassword
        private String password;

        @NotBlank
        private String name;

        @NotBlank
        private String gender;

        @Past
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private Date dob;

        public SignUpRequest(String username, String password, String name, String gender, Date dob) {
                this.username = username;
                this.password = password;
                this.name = name;
                this.gender = gender;
                this.dob = dob;
        }

        protected SignUpRequest() {
        }

        public String getUsername() {
                return username;
        }

        public String getPassword() {
                return password;
        }

        public String getName() {
                return name;
        }

        public String getGender() {
                return gender;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public void setName(String name) {
                this.name = name;
        }

        public void setGender(String gender) {
                this.gender = gender;
        }

        public void setDob(Date dob) {
                this.dob = dob;
        }

        public Date getDob() {
                return dob;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                SignUpRequest that = (SignUpRequest) o;
                return Objects.equals(username, that.username) &&
                        Objects.equals(password, that.password) &&
                        Objects.equals(name, that.name) &&
                        Objects.equals(gender, that.gender) &&
                        Objects.equals(dob, that.dob);
        }

        @Override
        public int hashCode() {
                return Objects.hash(username, password, name, gender, dob);
        }

        @Override
        public String toString() {
                return "SignUpRequest{" +
                        "username='" + username + '\'' +
                        ", password='[PROTECTED]'" +
                        ", name='" + name + '\'' +
                        ", gender='" + gender + '\'' +
                        ", dob=" + dob +
                        '}';
        }
}
