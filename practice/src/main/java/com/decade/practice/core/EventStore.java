package com.decade.practice.core;

import com.decade.practice.model.domain.entity.ChatEvent;

import java.util.Collection;

public interface EventStore {
      Collection<ChatEvent> save(ChatEvent event);
}