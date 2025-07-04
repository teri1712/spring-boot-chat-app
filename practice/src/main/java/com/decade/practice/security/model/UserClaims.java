package com.decade.practice.security.model;

import com.decade.practice.model.domain.entity.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;
import java.util.UUID;

@JsonDeserialize
public class UserClaims {
      private final UUID id;
      private final String username;
      private final String name;
      private final String role;
      private final String gender;

      public UserClaims(UUID id, String username, String name, String role, String gender) {
            this.id = id;
            this.username = username;
            this.name = name;
            this.role = role;
            this.gender = gender;
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