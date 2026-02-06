package com.decade.practice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventRequest {

    private TextEventRequest textEvent;
    private ImageEventRequest imageEvent;
    private IconEventRequest iconEvent;
    private PreferenceRequest preferenceEvent;
    private FileEventRequest fileEvent;
    private SeenEventRequest seenEvent;

}
