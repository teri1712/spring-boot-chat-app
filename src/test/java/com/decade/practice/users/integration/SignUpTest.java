package com.decade.practice.users.integration;

import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.RedisDataset;
import com.decade.practice.users.domain.events.UserCreated;
import com.decade.practice.users.dto.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ComponentTest(datasets = {UserDataset.class, RedisDataset.class})
@RequiredArgsConstructor
class SignUpTest {
    final MockMvc mockMvc;
    final ObjectMapper objectMapper;
    final ApplicationEvents events;


    @Test
    void givenValidSignUpRequest_whenSignUp_thenStatusCreated() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("newuser");
        request.setPassword("StrongPass123!");
        request.setName("New User");
        request.setGender(1.0f);
        request.setDob(Instant.now());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());


        assertThat(events.stream(UserCreated.class)).hasSize(1);
    }

    @Test
    void givenExistingUsername_whenSignUp_thenStatusConflict() throws Exception {
        // Seed alice first
        SignUpRequest aliceRequest = new SignUpRequest();
        aliceRequest.setUsername("alice");
        aliceRequest.setPassword("StrongPass123!");
        aliceRequest.setName("Alice Liddell");
        aliceRequest.setGender(2.0f);
        aliceRequest.setDob(Instant.now());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aliceRequest)))
            .andExpect(status().isCreated());

        SignUpRequest request = new SignUpRequest();
        request.setUsername("alice"); // Alice already exists
        request.setPassword("StrongPass123!");
        request.setName("Alice Again");
        request.setGender(2.0f);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Username already exists"));
    }

    @Test
    void givenWeakPassword_whenSignUp_thenStatusBadRequest() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("validuser");
        request.setPassword("weak");
        request.setName("Valid User");
        request.setGender(1.0f);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenUsernameWithSpaces_whenSignUp_thenStatusBadRequest() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("user customName");
        request.setPassword("StrongPass123!");
        request.setName("User Name");
        request.setGender(1.0f);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
