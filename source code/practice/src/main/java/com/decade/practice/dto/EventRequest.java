package com.decade.practice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventRequest {

    private TextEventDto textEvent;
    private ImageEventDto imageEvent;
    private IconEventDto iconEvent;
    private PreferenceRequest preferenceEvent;
    private FileEventDto fileEvent;
    private SeenEventDto seenEvent;


}
