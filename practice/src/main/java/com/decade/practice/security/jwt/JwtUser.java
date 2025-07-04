package com.decade.practice.security.jwt;

import com.decade.practice.model.domain.entity.User;
import com.decade.practice.security.model.UserClaims;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class JwtUser implements AuthenticatedPrincipal, Serializable {
      private final UUID id;
      private final String _username;

      public JwtUser(UUID id, String _username) {
            this.id = id;
            this._username = _username;
      }

      public JwtUser(User user) {
            this(user.getId(), user.getUsername());
      }

      public JwtUser(UserClaims userClaims) {
            this(userClaims.getId(), userClaims.getUsername());
      }

      public UUID getId() {
            return id;
      }

      @Override
      public String getName() {
            return _username;
      }

      @Override
      public int hashCode() {
            return Objects.hash(_username);
      }

      @Override
      public boolean equals(Object other) {
            if (other == null) return false;
            return other.hashCode() == hashCode();
      }
}