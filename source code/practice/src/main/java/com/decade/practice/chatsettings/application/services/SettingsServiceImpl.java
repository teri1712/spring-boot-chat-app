package com.decade.practice.chatsettings.application.services;

import com.decade.practice.chatsettings.api.SettingApi;
import com.decade.practice.chatsettings.api.SettingsInfo;
import com.decade.practice.chatsettings.application.ports.out.PreferenceNotifier;
import com.decade.practice.chatsettings.application.ports.out.SettingRepository;
import com.decade.practice.chatsettings.application.ports.out.ThemeRepository;
import com.decade.practice.chatsettings.domain.Preference;
import com.decade.practice.chatsettings.domain.Setting;
import com.decade.practice.chatsettings.dto.PreferenceMapper;
import com.decade.practice.chatsettings.dto.PreferenceRequest;
import com.decade.practice.chatsettings.dto.SettingsMapper;
import com.decade.practice.engagement.api.WritePolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SettingsServiceImpl implements SettingsService, SettingApi {

    final SettingRepository settings;

    final ThemeRepository themes;
    final SettingsMapper settingsMapper;

    final PreferenceMapper preferenceMapper;
    final PreferenceNotifier notifier;

    @Override
    @WritePolicy
    public void setPreference(String chatId, UUID userId, PreferenceRequest request) {
        Setting setting = settings.findByIdentifier(chatId).orElseThrow();
        Setting.PreferenceChain chain = setting.getPreferenceChain(userId);

        if (request.iconId() != null)
            chain = chain.iconId(request.iconId());

        if (request.themeId() != null)
            chain = chain.theme(themes.findById(request.themeId()).orElseThrow());

        if (request.customName() != null && !request.customName().isBlank())
            chain = chain.roomName(request.customName());

        if (request.customAvatar() != null && !request.customAvatar().isBlank())
            chain = chain.roomAvatar(request.customAvatar());

        Preference preference = chain.complete();
        setting.setPreference(userId, preference);
        settings.save(setting);

        notifier.notify(chatId, preferenceMapper.mapMessage(preference));
    }


    @Override
    public Map<String, SettingsInfo> find(Set<String> chatIds) {
        return settings.findByIdentifierIn(chatIds).stream().map(settingsMapper::map)
            .collect(Collectors.toMap(
                SettingsInfo::id,
                Function.identity(),
                (existing, replacement) -> existing
            ));
    }

    @Override
    public SettingsInfo create(String chatId, String roomName) {
        Setting setting = new Setting(chatId, new Preference(1, roomName, null, null));
        settings.save(setting);
        return settingsMapper.map(settings.save(setting));
    }
}
