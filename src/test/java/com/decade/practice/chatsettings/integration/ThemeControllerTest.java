package com.decade.practice.chatsettings.integration;

import com.decade.practice.common.ComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ComponentTest(datasets = SettingDataset.class)
class ThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void givenThemesExist_whenRequestThemes_thenReturnsAllThemes() throws Exception {
        mockMvc.perform(get("/themes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(5));
    }

}
