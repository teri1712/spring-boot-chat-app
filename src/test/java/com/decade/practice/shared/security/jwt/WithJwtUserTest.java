package com.decade.practice.shared.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@TestExecutionListeners({ WithSecurityContextTestExecutionListener.class })
class WithJwtUserTest {

    @Test
    @WithJwtUser(id = "22222222-2222-2222-2222-222222222222", username = "bob", name = "Bob Builder")
    void testWithJwtUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        assertThat(authentication).isInstanceOf(JwtUserAuthentication.class);
        JwtUserAuthentication jwtAuth = (JwtUserAuthentication) authentication;
        
        JwtUser principal = jwtAuth.getPrincipal();
        assertThat(principal.getUsername()).isEqualTo("bob");
        assertThat(principal.getId()).isEqualTo(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        assertThat(principal.getClaims().name()).isEqualTo("Bob Builder");
        
        assertThat(jwtAuth.getCredentials()).isNotNull();
        assertThat(jwtAuth.getCredentials().toString()).startsWith("eyJ"); // JWT header starts with eyJ
    }

    @Test
    @WithJwtUser
    void testWithJwtUserDefault() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        assertThat(authentication).isInstanceOf(JwtUserAuthentication.class);
        JwtUserAuthentication jwtAuth = (JwtUserAuthentication) authentication;
        
        JwtUser principal = jwtAuth.getPrincipal();
        assertThat(principal.getUsername()).isEqualTo("alice");
        assertThat(principal.getId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }
}
