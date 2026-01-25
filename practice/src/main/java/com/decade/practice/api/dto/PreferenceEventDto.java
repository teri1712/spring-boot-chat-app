package com.decade.practice.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PreferenceEventDto {
    private PreferenceDto preference;

    public PreferenceEventDto(PreferenceDto preference) {
        this.preference = preference;
    }

    protected PreferenceEventDto() {
    }

}
