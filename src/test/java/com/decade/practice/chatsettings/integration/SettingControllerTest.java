package com.decade.practice.chatsettings.integration;

import com.decade.practice.chatsettings.api.PreferenceInfo;
import com.decade.practice.chatsettings.api.SettingApi;
import com.decade.practice.chatsettings.api.SettingsInfo;
import com.decade.practice.chatsettings.application.ports.out.ThemeRepository;
import com.decade.practice.chatsettings.domain.Theme;
import com.decade.practice.chatsettings.domain.events.PreferenceChanged;
import com.decade.practice.chatsettings.dto.PreferenceRequest;
import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.security.jwt.WithJwtUser;
import com.decade.practice.engagement.api.EngagementApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@WithJwtUser(
    id = "11111111-1111-1111-1111-111111111111",
    name = "alice",
    username = "alice"
)
@ComponentTest(datasets = SettingDataset.class)
public class SettingControllerTest {

    final MockMvc mockMvc;
    final ObjectMapper objectMapper;
    final SettingApi settingApi;
    final ThemeRepository themeRepository;

    @Value("${broker.topics.setting}")
    String preferenceTopic;

    @MockitoSpyBean
    RedisTemplate<String, Object> redisTemplate;

    @MockitoSpyBean
    EngagementApi engagementApi;

    @BeforeEach
    void allowEngagement() {
        when(engagementApi.canRead(any(), any()))
            .thenReturn(true);

        when(engagementApi.canWrite(any(), any()))
            .thenReturn(true);
    }

    @Autowired
    ApplicationEvents events;

    @Test
    void givenExistingChat_whenAliceUpdatesPreference_thenPreferenceIsStored() throws Exception {
        // Given
        // Correct order: 1111... < 2222...
        // Create chat first

        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        settingApi.create(chatId, "hello");

        Theme theme = themeRepository.save(new Theme(null, "Vcl", "Vcl"));
        PreferenceRequest preference = new PreferenceRequest(99, "My pookie bob", theme.getId(), "Vcl");

        // When
        mockMvc.perform(patch("/chats/{id}/preference", chatId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preference)))
            .andExpect(status().isOk());

        await().atMost(Duration.ofSeconds(2))
            .untilAsserted(() ->
                assertThat(events.stream(PreferenceChanged.class))
                    .hasSize(1));
        // Then

        assertThat(settingApi.find(Set.of(chatId)).get(chatId))
            .extracting(SettingsInfo::preference)
            .extracting(PreferenceInfo::customName, PreferenceInfo::iconId)
            .containsExactly("My pookie bob", 99);

        verify(redisTemplate)
            .convertAndSend(eq(preferenceTopic + ":" + chatId), any());
    }
}
