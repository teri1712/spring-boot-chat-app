package com.decade.practice.engagement.api.events;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder

public class PreferenceEventPlaced extends EventPlaced {


    private final Integer iconId;
    private final String roomName;
    private final String roomAvatar;
    private final String theme;

}
