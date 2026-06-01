package com.decade.practice.live.infras.security;

import com.decade.practice.shared.security.jwt.JwtUser;

import java.security.Principal;

public record SocketAuthentication(JwtUser jwtUser, String accessToken) implements Principal {
    @Override
    public String getName() {
        return jwtUser.getId().toString();
    }
}