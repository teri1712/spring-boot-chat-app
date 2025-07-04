package com.decade.practice.model.domain;

import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.User;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;
import java.util.UUID;

@JsonDeserialize
@JsonSerialize
public class TypeEvent {
    private final UUID from;
    private final ChatIdentifier chat;
    private final long time;
    private final String key;

    public TypeEvent(UUID from, ChatIdentifier chat) {
        this(from, chat, System.currentTimeMillis());
    }

    public TypeEvent(UUID from, ChatIdentifier chat, long time) {
        this.from = from;
        this.chat = chat;
        this.time = time;
        this.key = determineKey(from, chat);
    }

    public TypeEvent(User owner, Chat chat) {
        this(owner.getId(), chat.getIdentifier());
    }

    public UUID getFrom() {
        return from;
    }

    public ChatIdentifier getChat() {
        return chat;
    }

    public long getTime() {
        return time;
    }

    public String getKey() {
        return key;
    }

    /**
     * Determines a key based on the from UUID and chat identifier.
     * This is a utility method that was a top-level function in the Kotlin version.
     */
    public static String determineKey(UUID from, ChatIdentifier chat) {
        UUID partner = from.equals(chat.getFirstUser()) ? chat.getSecondUser() : chat.getFirstUser();
        return from + "->" + partner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeEvent typeEvent = (TypeEvent) o;
        return time == typeEvent.time &&
                Objects.equals(from, typeEvent.from) &&
                Objects.equals(chat, typeEvent.chat) &&
                Objects.equals(key, typeEvent.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, chat, time, key);
    }

    @Override
    public String toString() {
        return "TypeEvent{" +
                "from=" + from +
                ", chat=" + chat +
                ", time=" + time +
                ", key='" + key + '\'' +
                '}';
    }
}