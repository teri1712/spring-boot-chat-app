package com.decade.practice.web.security;

import java.util.UUID;

public record UserClaims(
        UUID id,
        String username,
        String name,
        String avatar
) {

}