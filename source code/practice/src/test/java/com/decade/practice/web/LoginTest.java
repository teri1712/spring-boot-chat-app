package com.decade.practice.web;

import com.decade.practice.common.BaseTestClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LoginTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenValidCredentials_whenLogin_thenReturnsAccountAndToken() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "alice")
                        .param("password", "Password123!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account", notNullValue()))
                .andExpect(jsonPath("$.tokenCredential", notNullValue()))
                .andExpect(jsonPath("$.account.user.username").value("alice"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenInvalidPassword_whenLogin_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "alice")
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Wrong password"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    void givenNonExistentUser_whenLogin_thenReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "nonexistent")
                        .param("password", "anypassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Username not found"));
    }
}