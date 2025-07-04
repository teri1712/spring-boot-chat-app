package com.decade.practice.model.domain;

import com.decade.practice.model.domain.entity.ChatEvent;
import com.decade.practice.model.local.Conversation;

import java.util.List;
import java.util.Objects;

public class ChatSnapshot {
    private final Conversation conversation;
    private final List<ChatEvent> eventList;
    private final int atVersion;

    public ChatSnapshot(Conversation conversation, List<ChatEvent> eventList, int atVersion) {
        this.conversation = conversation;
        this.eventList = eventList;
        this.atVersion = atVersion;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public List<ChatEvent> getEventList() {
        return eventList;
    }

    public int getAtVersion() {
        return atVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatSnapshot that = (ChatSnapshot) o;
        return atVersion == that.atVersion &&
                Objects.equals(conversation, that.conversation) &&
                Objects.equals(eventList, that.eventList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversation, eventList, atVersion);
    }

    @Override
    public String toString() {
        return "ChatSnapshot{" +
                "conversation=" + conversation +
                ", eventList=" + eventList +
                ", atVersion=" + atVersion +
                '}';
    }
}