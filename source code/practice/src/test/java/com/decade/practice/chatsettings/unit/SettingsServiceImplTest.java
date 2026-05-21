package com.decade.practice.chatsettings.unit;

import com.decade.practice.chatsettings.application.ports.out.PreferenceNotifier;
import com.decade.practice.chatsettings.application.ports.out.SettingRepository;
import com.decade.practice.chatsettings.application.ports.out.ThemeRepository;
import com.decade.practice.chatsettings.application.services.SettingsServiceImpl;
import com.decade.practice.chatsettings.domain.Preference;
import com.decade.practice.chatsettings.domain.Setting;
import com.decade.practice.chatsettings.domain.Theme;
import com.decade.practice.chatsettings.domain.messages.PreferenceMessage;
import com.decade.practice.chatsettings.dto.PreferenceMapper;
import com.decade.practice.chatsettings.dto.PreferenceRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsServiceImplTest {

    @Mock
    SettingRepository settings;

    @Mock
    ThemeRepository themes;

    @Mock
    PreferenceMapper preferenceMapper;

    @Mock
    PreferenceNotifier notifier;

    @InjectMocks
    SettingsServiceImpl settingsService;

    @Test
    void givenPreferenceRequest_whenSetPreference_thenSaveAndNotify() {
        // Given
        String chatId = "chat-123";
        UUID userId = UUID.randomUUID();
        PreferenceRequest request = new PreferenceRequest(1, "New Name", null, "avatar.jpg");
        
        Setting setting = new Setting(chatId, new Preference(0, "Old Name", null, null));
        when(settings.findByIdentifier(chatId)).thenReturn(Optional.of(setting));
        
        PreferenceMessage mappedMessage = PreferenceMessage.builder().build();
        when(preferenceMapper.mapMessage(any(Preference.class))).thenReturn(mappedMessage);

        // When
        settingsService.setPreference(chatId, userId, request);

        // Then
        verify(settings).save(setting);
        
        ArgumentCaptor<Preference> preferenceCaptor = ArgumentCaptor.forClass(Preference.class);
        verify(preferenceMapper).mapMessage(preferenceCaptor.capture());
        
        Preference updatedPreference = preferenceCaptor.getValue();
        assertThat(updatedPreference.iconId()).isEqualTo(1);
        assertThat(updatedPreference.customName()).isEqualTo("New Name");
        assertThat(updatedPreference.customAvatar()).isEqualTo("avatar.jpg");
        
        verify(notifier).notify(eq(chatId), eq(mappedMessage));
    }

    @Test
    void givenPreferenceRequestWithTheme_whenSetPreference_thenSaveAndNotify() {
        // Given
        String chatId = "chat-123";
        UUID userId = UUID.randomUUID();
        Long themeId = 100L;
        PreferenceRequest request = new PreferenceRequest(null, null, themeId, null);

        Setting setting = new Setting(chatId, new Preference(0, "Old Name", null, null));
        when(settings.findByIdentifier(chatId)).thenReturn(Optional.of(setting));

        Theme theme = new Theme(themeId, "background.jpg", "Cool Theme");
        when(themes.findById(themeId)).thenReturn(Optional.of(theme));

        PreferenceMessage mappedMessage = PreferenceMessage.builder().build();
        when(preferenceMapper.mapMessage(any(Preference.class))).thenReturn(mappedMessage);

        // When
        settingsService.setPreference(chatId, userId, request);

        // Then
        verify(settings).save(setting);

        ArgumentCaptor<Preference> preferenceCaptor = ArgumentCaptor.forClass(Preference.class);
        verify(preferenceMapper).mapMessage(preferenceCaptor.capture());

        Preference updatedPreference = preferenceCaptor.getValue();
        assertThat(updatedPreference.theme()).isEqualTo(theme);

        verify(notifier).notify(eq(chatId), eq(mappedMessage));
    }
}
