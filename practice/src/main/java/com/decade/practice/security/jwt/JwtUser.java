package com.decade.practice.security.jwt;

import com.decade.practice.entities.domain.entity.User;
import com.decade.practice.security.model.UserClaims;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class JwtUser implements AuthenticatedPrincipal, Serializable {

      private UUID id;
      private String username;

      public JwtUser(UUID id, String username) {
            this.id = id;
            this.username = username;
      }

      protected JwtUser() {
      }

      public JwtUser(User user) {
            this(user.getId(), user.getUsername());
      }

      public void setId(UUID id) {
            this.id = id;
      }

      public String getUsername() {
            return username;
      }

      public void setUsername(String username) {
            this.username = username;
      }

      public JwtUser(UserClaims userClaims) {
            this(userClaims.getId(), userClaims.getUsername());
      }

      public UUID getId() {
            return id;
      }

      @Override
      public String getName() {
            return username;
      }

      @Override
      public int hashCode() {
            return Objects.hash(username);
      }

      @Override
      public boolean equals(Object other) {
            if (other == null) return false;
            return other.hashCode() == hashCode();
      }
}