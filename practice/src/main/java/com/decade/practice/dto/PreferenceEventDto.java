package com.decade.practice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PreferenceEventDto {
    private PreferenceResponse preference;

    public PreferenceEventDto(PreferenceResponse preference) {
        this.preference = preference;
    }

    protected PreferenceEventDto() {
    }

}
