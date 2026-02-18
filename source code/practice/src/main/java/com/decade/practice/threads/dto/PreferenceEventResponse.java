package com.decade.practice.threads.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PreferenceEventResponse extends EventResponse {
    private final Integer iconId;
    private final String roomName;
    private final String roomAvatar;
    private final String theme;

}
