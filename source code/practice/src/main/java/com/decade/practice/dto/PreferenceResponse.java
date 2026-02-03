package com.decade.practice.dto;

import com.decade.practice.persistence.jpa.embeddables.Preference;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PreferenceResponse {
    private int iconId;
    private String roomName;

    private ThemeDto theme;

    public PreferenceResponse() {
    }


    public static PreferenceResponse from(Preference preference) {
        PreferenceResponse dto = new PreferenceResponse();
        dto.setIconId(preference.getIconId());
        dto.setRoomName(preference.getRoomName());
        if (preference.getTheme() != null) {
            dto.setTheme(ThemeDto.from(preference.getTheme()));
        }
        return dto;
    }
}
