package com.decade.practice.chatsettings.integration;

import com.decade.practice.chatorchestrator.application.ports.in.ChatService;
import com.decade.practice.chatsettings.application.ports.out.SettingRepository;
import com.decade.practice.chatsettings.application.services.SettingsService;
import com.decade.practice.chatsettings.domain.Preference;
import com.decade.practice.chatsettings.domain.Setting;
import com.decade.practice.chatsettings.dto.PreferenceRequest;
import com.decade.practice.common.ComponentTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@RequiredArgsConstructor
@ComponentTest(datasets = SettingDataset.class)
class SettingServiceTest {

    final SettingRepository settings;
    final SettingsService settingsService;
    final ChatService chatService;

    @Test
    void givenExistingChat_whenAliceUpdatesPreference_thenPreferenceIsStored() throws Exception {
        // Given
        // Correct order: 1111... < 2222...
        // Create chat first
        chatService.getDirect(UUID.fromString("11111111-1111-1111-1111-111111111111"), UUID.fromString("22222222-2222-2222-2222-222222222222"));

        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        PreferenceRequest preference = new PreferenceRequest(99, "My pookie bob", null, "Vcl");

        settingsService.setPreference(chatId, UUID.fromString("11111111-1111-1111-1111-111111111111"), preference);

        // Then
        assertThat(settings.findAll())
            .extracting(Setting::getPreference)
            .extracting(Preference::customName)
            .containsExactly("My pookie bob");
    }

}
