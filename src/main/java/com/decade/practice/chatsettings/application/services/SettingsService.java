package com.decade.practice.chatsettings.application.services;

import com.decade.practice.chatsettings.dto.PreferenceRequest;

import java.util.UUID;

public interface SettingsService {

      void setPreference(String chatId, UUID userId, PreferenceRequest request);


}