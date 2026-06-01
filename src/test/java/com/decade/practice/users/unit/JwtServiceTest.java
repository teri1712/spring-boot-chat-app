package com.decade.practice.users.unit;

import com.decade.practice.shared.security.UserClaims;
import com.decade.practice.shared.security.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService = new JwtService("vcl-vcl-vcl-vcl-vcl-vcl-vcl-vcl-vcl-vcl");

    @Test
    void givenTokenWith2SecExp_whenAfter2Sec_thenTheTokenMustBeInvalided() {
        // given

        String token = jwtService.encodeToken(new UserClaims(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "alice",
            "alice",
            "luffy.jpg"
        ), ofSeconds(2));
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertThatThrownBy(() -> jwtService.decodeToken(token))
            .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void givenTokenWith2SecExp_whenAfter1Sec_thenTheTokenStillValid() {
        // given

        String token = jwtService.encodeToken(new UserClaims(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "alice",
            "alice",
            "luffy.jpg"
        ), ofSeconds(2));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertThat(jwtService.decodeToken(token)).extracting(UserClaims::username).isEqualTo("alice");
    }

}
