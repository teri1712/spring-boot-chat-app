package com.decade.practice.users.integration;


import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.RedisDataset;
import com.decade.practice.users.application.ports.in.ProfileService;
import com.decade.practice.users.application.ports.in.TokenSessionService;
import com.decade.practice.users.application.ports.out.TokenStore;
import com.decade.practice.users.application.ports.out.UserRepository;
import com.decade.practice.users.dto.AccountResponse;
import com.decade.practice.users.dto.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentTest(datasets = {UserDataset.class, RedisDataset.class})
@RequiredArgsConstructor
class TokenSessionTest {
    final TokenStore tokenStore;
    final UserRepository users;
    final TokenSessionService tokenSessionService;
    final ProfileService profileService;
    final MockMvc mockMvc;
    final ObjectMapper objectMapper;


    @BeforeEach
    void signUpAlice() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("alice");
        request.setPassword("Password123!");
        request.setName("Alice Liddell");
        request.setGender(1.0f);
        request.setDob(Instant.now());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

    }

    @Test
    void given2ActiveSessionsOfAlice_whenLogOutTheFirstOne_thenOnlyTheSecondRemainsActive() throws Exception {
        // given
        tokenStore.add("alice", "token1");
        tokenStore.add("alice", "token2");

        tokenSessionService.logout("alice", "token1");

        assertThat(tokenStore.has("alice", "token2")).isTrue();
        assertThat(tokenStore.has("alice", "token1")).isFalse();
        assertThat(tokenStore.size("alice")).isEqualTo(1);

    }


    @Test
    void given1ActiveSessionsOfAlice_whenLoginIn_thenTwoSessionsAreActive() throws Exception {
        // given
        tokenStore.add("alice", "token1");
        assertThat(tokenStore.size("alice")).isEqualTo(1);

        String token2 = tokenSessionService.login("alice").getAccessToken().refreshToken();

        assertThat(tokenStore.has("alice", "token1")).isTrue();
        assertThat(tokenStore.has("alice", token2)).isTrue();
        assertThat(tokenStore.size("alice")).isEqualTo(2);

    }

    @Test
    void given2ActiveSessionsOfAlice_whenFirstSessionChangingPassword_thenTwoSessionsBecomesInvalidated() throws Exception {
        // given
        UUID aliceId = users.findByUsername("alice").orElseThrow().getId();
        tokenStore.add("alice", "token1");
        tokenStore.add("alice", "token2");

        AccountResponse account = profileService.changePassword(aliceId, "new_password", "Password123!");
        assertThat(tokenStore.size("alice")).isOne();

        assertThat(tokenStore.has("alice", "token1")).isFalse();
        assertThat(tokenStore.has("alice", "token2")).isFalse();

        assertThat(tokenStore.has("alice", account.getAccessToken().refreshToken())).isTrue();
    }

}
