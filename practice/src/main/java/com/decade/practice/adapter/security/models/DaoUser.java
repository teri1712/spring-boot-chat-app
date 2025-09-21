package com.decade.practice.adapter.security.models;

import com.decade.practice.domain.entities.User;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class DaoUser implements UserDetails, AuthenticatedPrincipal {

        private final UUID id;
        private final String password;
        private final String username;
        private final String role;

        public DaoUser(User user) {
                this.id = user.getId();
                this.password = user.getPassword();
                this.username = user.getUsername();
                this.role = user.getRole();
        }

        public UUID getId() {
                return id;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.singletonList(new SimpleGrantedAuthority(role));
        }

        @Override
        public String getPassword() {
                return password;
        }

        @Override
        public String getUsername() {
                return username;
        }

        @Override
        public boolean isAccountNonExpired() {
                return true;
        }

        @Override
        public boolean isAccountNonLocked() {
                return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
                return true;
        }

        @Override
        public boolean isEnabled() {
                return true;
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

        @Override
        public String getName() {
                return username;
        }
}