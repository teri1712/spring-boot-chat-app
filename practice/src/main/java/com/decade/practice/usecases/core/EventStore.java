package com.decade.practice.usecases.core;

import com.decade.practice.entities.domain.entity.ChatEvent;

import java.util.Collection;

public interface EventStore {
      Collection<ChatEvent> save(ChatEvent event);
}