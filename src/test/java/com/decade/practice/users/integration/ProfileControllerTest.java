package com.decade.practice.users.integration;

import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.RedisDataset;
import com.decade.practice.users.domain.events.UserPasswordChanged;
import com.decade.practice.users.dto.ProfileRequest;
import com.decade.practice.users.dto.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentTest(datasets = {UserDataset.class, RedisDataset.class})
@WithUserDetails(value = "alice", setupBefore = TestExecutionEvent.TEST_EXECUTION)
@RequiredArgsConstructor
class ProfileControllerTest {
    final MockMvc mockMvc;
    final ObjectMapper objectMapper;

    @Autowired
    ApplicationEvents events;

    @BeforeEach
    void setupAlice() throws Exception {
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
    void givenAliceExists_whenAliceRequestsAccount_thenReturnsAliceProfile() throws Exception {
        mockMvc.perform(get("/profiles/me")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("alice"))
            .andExpect(jsonPath("$.name").value("Alice Liddell"));
    }

    @Test
    void givenAliceExists_whenAliceChangesProfileName_thenNameIsUpdated() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setName("Alice in Wonderland");
        request.setGender(2.0f);

        mockMvc.perform(patch("/profiles/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice in Wonderland"));
    }

    @Test
    void givenAliceExists_whenAliceChangesToStrongPassword_thenSucceeds() throws Exception {
        mockMvc.perform(post("/profiles/me/password")
                .param("password", "Password123!") // alice's seed password
                .param("new_password", "NewStrongPass123!"))
            .andExpect(status().isOk());


        assertThat(events.stream(UserPasswordChanged.class)).hasSize(1);
    }

    @Test
    void givenAliceExists_whenAliceChangesToWeakPassword_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/profiles/me/password")
                .param("password", "Password123!").param("new_password", "weak"))
            .andExpect(status().isBadRequest());
    }
}
