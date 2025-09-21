package com.decade.practice.application.usecases;

import com.decade.practice.domain.entities.ChatEvent;

import java.util.Collection;

public interface EventStore {
        Collection<ChatEvent> save(ChatEvent event);
}