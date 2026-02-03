package com.decade.practice.web;

import com.decade.practice.dto.SignUpRequest;
import com.decade.practice.common.BaseTestClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SignUpTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Sql(scripts = "/sql/clean.sql")
    void givenValidSignUpRequest_whenSignUp_thenStatusCreated() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("newuser");
        request.setPassword("StrongPass123!");
        request.setName("New User");
        request.setGender(1.0f);
        request.setDob(new Date(System.currentTimeMillis() - 1000000000L));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenExistingUsername_whenSignUp_thenStatusConflict() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("alice"); // Alice already exists in seed_users.sql
        request.setPassword("StrongPass123!");
        request.setName("Alice Again");
        request.setGender(2.0f);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Username already exists"));
    }

    @Test
    @Sql(scripts = "/sql/clean.sql")
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
    @Sql(scripts = "/sql/clean.sql")
    void givenUsernameWithSpaces_whenSignUp_thenStatusBadRequest() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("user name");
        request.setPassword("StrongPass123!");
        request.setName("User Name");
        request.setGender(1.0f);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}