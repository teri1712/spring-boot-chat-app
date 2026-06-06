package com.decade.practice.chat.integration;

import com.decade.practice.chatorchestrator.application.ports.in.ChatService;
import com.decade.practice.chatsettings.application.ports.out.SettingRepository;
import com.decade.practice.chatsettings.application.services.SettingsService;
import com.decade.practice.chatsettings.domain.Preference;
import com.decade.practice.chatsettings.domain.Setting;
import com.decade.practice.chatsettings.dto.PreferenceRequest;
import com.decade.practice.integration.BaseTestClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SettingServiceTest extends BaseTestClass {

    @Autowired
    SettingRepository settings;

    @Autowired
    SettingsService settingsService;

    @Autowired
    ChatService chatService;

    @Test
    @Sql(scripts = {"/sql/clean.sql", "/sql/seed_users.sql", "/sql/seed_themes.sql"})
    @WithUserDetails("alice")
    void givenExistingChat_whenAliceUpdatesPreference_thenPreferenceIsStored() throws Exception {
        // Given
        // Correct order: 1111... < 2222...
        // Create chat first
        chatService.getDirect(UUID.fromString("11111111-1111-1111-1111-111111111111"), UUID.fromString("22222222-2222-2222-2222-222222222222"));

        String chatId = "11111111-1111-1111-1111-111111111111+22222222-2222-2222-2222-222222222222";
        PreferenceRequest preference = new PreferenceRequest(99, "My pookie bob", 3L, "Vcl");

        settingsService.setPreference(chatId, UUID.fromString("11111111-1111-1111-1111-111111111111"), preference);

        // Then
        assertThat(settings.findAll())
            .extracting(Setting::getPreference)
            .extracting(Preference::customName)
            .containsExactly("My pookie bob");
    }

}
