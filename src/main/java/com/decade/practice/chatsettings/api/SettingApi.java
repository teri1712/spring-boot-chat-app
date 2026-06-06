package com.decade.practice.chatsettings.api;

import java.util.Map;
import java.util.Set;

public interface SettingApi {

      Map<String, SettingsInfo> find(Set<String> chatIds);

      SettingsInfo create(String chatId, String roomName);
}
