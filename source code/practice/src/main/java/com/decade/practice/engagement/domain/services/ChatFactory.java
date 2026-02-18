package com.decade.practice.engagement.domain.services;

import com.decade.practice.engagement.domain.Chat;
import com.decade.practice.engagement.domain.ChatCreators;
import com.decade.practice.engagement.domain.ChatPolicy;
import com.decade.practice.engagement.domain.Preference;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class ChatFactory {
    protected final ChatIdentifierMaker chatIdentifierMaker;

    public Chat create(@NotNull ChatCreators creators, Integer maximumParticipants, @Nullable String roomName) {
        String chatId = chatIdentifierMaker.make(creators);
        return new Chat(creators, chatId, new Preference(1, roomName, null, null), new ChatPolicy(maximumParticipants));
    }

}
