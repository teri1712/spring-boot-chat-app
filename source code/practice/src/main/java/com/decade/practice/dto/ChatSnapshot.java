package com.decade.practice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChatSnapshot {

    private Conversation conversation;
    private List<EventDto> eventList;
    private int atVersion;

}