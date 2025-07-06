package com.decade.practice.security.model;

import com.decade.practice.model.domain.entity.User;

import java.util.Objects;
import java.util.UUID;

public class UserClaims {
      private UUID id;
      private String username;
      private String name;
      private String role;
      private String gender;

      public UserClaims(UUID id, String username, String name, String role, String gender) {
            this.id = id;
            this.username = username;
            this.name = name;
            this.role = role;
            this.gender = gender;
      }

      protected UserClaims() {
      }

      public UserClaims(User user) {
            this(
                  user.getId(),
                  user.getUsername(),
                  user.getName(),
                  user.getRole(),
                  user.getGender().iterator().next() // Equivalent to random() in Kotlin for a single element
            );
      }

      public UUID getId() {
            return id;
      }

      public String getUsername() {
            return username;
      }

      public String getName() {
            return name;
      }

      public void setId(UUID id) {
            this.id = id;
      }

      public void setUsername(String username) {
            this.username = username;
      }

      public void setName(String name) {
            this.name = name;
      }

      public void setRole(String role) {
            this.role = role;
      }

      public void setGender(String gender) {
            this.gender = gender;
      }

      public String getRole() {
            return role;
      }

      public String getGender() {
            return gender;
      }

      @Override
      public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserClaims that = (UserClaims) o;
            return Objects.equals(id, that.id) &&
                  Objects.equals(username, that.username) &&
                  Objects.equals(name, that.name) &&
                  Objects.equals(role, that.role) &&
                  Objects.equals(gender, that.gender);
      }

      @Override
      public int hashCode() {
            return Objects.hash(id, username, name, role, gender);
      }

      @Override
      public String toString() {
            return "UserClaims{" +
                  "id=" + id +
                  ", username='" + username + '\'' +
                  ", name='" + name + '\'' +
                  ", role='" + role + '\'' +
                  ", gender='" + gender + '\'' +
                  '}';
      }
}