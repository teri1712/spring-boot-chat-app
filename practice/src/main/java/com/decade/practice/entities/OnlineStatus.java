package com.decade.practice.entities;

import com.decade.practice.entities.domain.entity.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;
import java.util.Objects;

@JsonDeserialize
@RedisHash(value = "ONLINE", timeToLive = 5 * 60L)
public class OnlineStatus {

      @Id
      private String username;
      private long at;

      @Transient
      private User user;

      public OnlineStatus(String username, long at) {
            this.username = username;
            this.at = at;
      }

      public OnlineStatus(User user, long at) {
            this(user.getUsername(), at);
            this.user = user;
      }

      public OnlineStatus(User user) {
            this(user, Instant.now().getEpochSecond());
      }

      public String getUsername() {
            return username;
      }

      public long getAt() {
            return at;
      }

      public User getUser() {
            return user;
      }

      public void setUser(User user) {
            this.user = user;
      }

      @Override
      public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OnlineStatus that = (OnlineStatus) o;
            return at == that.at && Objects.equals(username, that.username);
      }

      @Override
      public int hashCode() {
            return Objects.hash(username, at);
      }

      @Override
      public String toString() {
            return "OnlineStatus{" +
                  "username='" + username + '\'' +
                  ", at=" + at +
                  ", user=" + user +
                  '}';
      }
}