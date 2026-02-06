package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.Preference;

public record PreferenceResponse(int iconId, String roomName, ThemeResponse theme) {
    public static PreferenceResponse from(Preference preference) {
        if (preference == null) return null;
        return new PreferenceResponse(
                preference.getIconId(),
                preference.getRoomName(),
                ThemeResponse.from(preference.getTheme())
        );
    }
}
