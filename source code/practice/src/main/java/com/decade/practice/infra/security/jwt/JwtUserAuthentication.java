package com.decade.practice.infra.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Objects;

public class JwtUserAuthentication extends AbstractAuthenticationToken {

    private final JwtUser jwtUser;
    private final String accessToken;

    public JwtUserAuthentication(JwtUser jwtUser, String accessToken) {
        super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        this.jwtUser = jwtUser;
        this.accessToken = accessToken;
        setAuthenticated(true);
        setDetails(jwtUser);
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public JwtUser getPrincipal() {
        return jwtUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JwtUserAuthentication that = (JwtUserAuthentication) o;
        return Objects.equals(jwtUser, that.jwtUser) &&
                Objects.equals(accessToken, that.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), jwtUser, accessToken);
    }
}