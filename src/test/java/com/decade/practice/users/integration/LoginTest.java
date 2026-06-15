package com.decade.practice.users.integration;

import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.RedisDataset;
import com.decade.practice.users.dto.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentTest(datasets = {UserDataset.class, RedisDataset.class})
class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private void signUpAlice() throws Exception {
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
    void givenValidCredentials_whenLogin_thenReturnsAccountAndToken() throws Exception {
        signUpAlice();
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "alice")
                .param("password", "Password123!"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profile", notNullValue()))
            .andExpect(jsonPath("$.accessToken", notNullValue()))
            .andExpect(jsonPath("$.profile.username").value("alice"));
    }

    @Test
    void givenInvalidPassword_whenLogin_thenReturnsUnauthorized() throws Exception {
        signUpAlice();
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "alice")
                .param("password", "wrongpassword"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.detail").value("Wrong password"));
    }

    @Test
    void givenNonExistentUser_whenLogin_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "nonexistent")
                .param("password", "anypassword"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.detail").value("Username not found"));
    }
}
