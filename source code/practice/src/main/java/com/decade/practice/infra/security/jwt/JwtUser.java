package com.decade.practice.infra.security.jwt;

import com.decade.practice.infra.security.models.UserClaims;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JwtUser implements AuthenticatedPrincipal, Serializable {

    private UserClaims claims;

    public String getUsername() {
        return claims.getUsername();
    }

    public UUID getId() {
        return claims.getId();
    }

    @Override
    public String getName() {
        return getUsername();
    }

    @Override
    public int hashCode() {
        return Objects.hash(claims.getId());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        return other.hashCode() == hashCode();
    }
}