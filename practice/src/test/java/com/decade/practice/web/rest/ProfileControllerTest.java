package com.decade.practice.web.rest;

import com.decade.practice.api.dto.ProfileRequest;
import com.decade.practice.common.BaseTestClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ProfileControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceExists_whenAliceRequestsAccount_thenReturnsAliceProfile() throws Exception {
        mockMvc.perform(get("/accounts/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.account.user.username").value("alice"))
                .andExpect(jsonPath("$.account.user.name").value("Alice Liddell"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceExists_whenAliceChangesProfileName_thenNameIsUpdated() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setName("Alice in Wonderland");
        request.setDob(new Date());
        request.setGender(2.0f);

        mockMvc.perform(put("/accounts/me/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice in Wonderland"));
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceExists_whenAliceChangesToStrongPassword_thenSucceeds() throws Exception {
        mockMvc.perform(post("/accounts/me/profile/password")
                        .param("password", "Password123!") // alice's seed password
                        .param("new_password", "NewStrongPass123!"))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql"})
    @WithUserDetails("alice")
    void givenAliceExists_whenAliceChangesToWeakPassword_thenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/accounts/me/profile/password")
                        .param("password", "Password123!")
                        .param("new_password", "weak"))
                .andExpect(status().isBadRequest());
    }
}
