package com.decade.practice.usecases;

import com.decade.practice.models.domain.entity.ChatEvent;

import java.util.Collection;

public interface EventStore {
        Collection<ChatEvent> save(ChatEvent event);
}