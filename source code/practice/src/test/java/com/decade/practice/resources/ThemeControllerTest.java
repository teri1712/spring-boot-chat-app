package com.decade.practice.resources;

import com.decade.practice.integration.BaseTestClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ThemeControllerTest extends BaseTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_themes.sql"})
    @WithUserDetails("alice")
    void givenThemesExist_whenRequestThemes_thenReturnsAllThemes() throws Exception {
        mockMvc.perform(get("/themes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }

}
