package com.decade.practice.web.rest;

import com.decade.practice.application.usecases.UserPresenceService;
import com.decade.practice.common.BaseTestClass;
import com.decade.practice.infra.security.jwt.JwtUser;
import com.decade.practice.infra.security.models.UserClaims;
import com.decade.practice.persistence.jpa.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PresenceControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserPresenceService presenceService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        // Strict isolation: clear Redis before each test
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceAndBobAreOnline_whenAliceListsOnline_thenReturnsBobOnly() throws Exception {
        // Given
        // Alice and Bob are online
        JwtUser alice = createJwtUser("11111111-1111-1111-1111-111111111111", "alice", "Alice Liddell");
        JwtUser bob = createJwtUser("22222222-2222-2222-2222-222222222222", "bob", "Bob Builder");

        presenceService.set(alice, Instant.now());
        presenceService.set(bob, Instant.now());

        // When & Then
        mockMvc.perform(get("/presences")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("bob"))
                .andExpect(jsonPath("$[0].name").value("Bob Builder"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenBobIsOnline_whenAliceGetsBobPresence_thenReturnsBobPresence() throws Exception {
        // Given
        JwtUser bob = createJwtUser("22222222-2222-2222-2222-222222222222", "bob", "Bob Builder");
        presenceService.set(bob, Instant.now());

        // When & Then
        mockMvc.perform(get("/presences/{username}", "bob")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("bob"))
                .andExpect(jsonPath("$.name").value("Bob Builder"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenUserNotFound_whenGetPresence_thenReturnsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/presences/{username}", "nonexistent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private JwtUser createJwtUser(String id, String username, String name) {
        UserClaims claims = UserClaims.builder()
                .id(UUID.fromString(id))
                .username(username)
                .name(name)
                .role("ROLE_USER")
                .gender(User.FEMALE)
                .build();
        return new JwtUser(claims);
    }
}
