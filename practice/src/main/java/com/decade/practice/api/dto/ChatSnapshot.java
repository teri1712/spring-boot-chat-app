package com.decade.practice.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatSnapshot {

    private Conversation conversation;
    private List<EventDto> eventList;
    private int atVersion;

    public ChatSnapshot(Conversation conversation, List<EventDto> eventList, int atVersion) {
        this.conversation = conversation;
        this.eventList = eventList;
        this.atVersion = atVersion;
    }

}