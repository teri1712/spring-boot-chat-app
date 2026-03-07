package com.decade.practice.chatsettings.api;

import java.time.Instant;

public record SettingsInfo(String id, Instant lastActivity, PreferenceInfo preference) {
}
