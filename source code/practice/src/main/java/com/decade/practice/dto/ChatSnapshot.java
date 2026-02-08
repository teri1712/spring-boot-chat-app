package com.decade.practice.dto;

import java.util.List;

public record ChatSnapshot(
        Conversation conversation,
        List<EventResponse> eventList,
        int atVersion

) {
}