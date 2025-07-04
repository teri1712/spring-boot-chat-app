package com.decade.practice.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Objects;

public class JwtUserAuthentication extends AbstractAuthenticationToken {
      private final JwtUser claims;
      private final String accessToken;

      public JwtUserAuthentication(JwtUser claims, String accessToken) {
            super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            this.claims = claims;
            this.accessToken = accessToken;
            setAuthenticated(true);
            setDetails(claims);
      }

      @Override
      public Object getCredentials() {
            return accessToken;
      }

      @Override
      public Object getPrincipal() {
            return claims;
      }

      @Override
      public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            JwtUserAuthentication that = (JwtUserAuthentication) o;
            return Objects.equals(claims, that.claims) &&
                  Objects.equals(accessToken, that.accessToken);
      }

      @Override
      public int hashCode() {
            return Objects.hash(super.hashCode(), claims, accessToken);
      }
}