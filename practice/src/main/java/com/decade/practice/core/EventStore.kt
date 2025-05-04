package com.decade.practice.core

import com.decade.practice.model.domain.entity.ChatEvent

interface EventStore {
      // return generated message events in database
      fun save(event: ChatEvent): Collection<ChatEvent>
}
