package com.decade.practice.users.domain;

import com.decade.practice.users.domain.events.UserCreated;
import com.decade.practice.users.domain.events.UserPasswordChanged;
import com.decade.practice.users.utils.GenderUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_member")
public class User extends AbstractAggregateRoot<User> {

      @Column(unique = true, nullable = false, updatable = false)
      private String username;

      @Column(nullable = false)
      private String password;

      private String name;

      private Instant dob;

      @Column(updatable = false)
      private String role = "ROLE_USER";

      @Id
      private UUID id;

      private String avatar;

      @Version
      private Integer version;

      @Column(nullable = false)
      private Float gender;

      public void changeGender(@NotNull Float gender) {
            this.gender = gender;
      }

      public void changeAvatar(@NotNull String avatar) {
            this.avatar = avatar;
      }

      void changePassword(@NotNull String password) {
            this.password = password;

            registerEvent(new UserPasswordChanged(username));
      }

      public void changeName(@NotNull String name) {
            this.name = name;
      }

      public void changeDob(@NotNull Instant dob) {
            this.dob = dob;
      }

      protected User() {
      }

      public User(UUID id, String username, String password, String name, String avatar, Instant dob, Float gender) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.name = name;
            this.avatar = avatar;
            this.dob = dob;
            this.gender = gender;

      }

      @PrePersist
      void onCreated() {
            registerEvent(new UserCreated(id, username, name, GenderUtils.inspect(gender), dob, avatar));
      }

}

