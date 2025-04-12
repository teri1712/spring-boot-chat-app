package com.decade.practice.database

import com.decade.practice.model.entity.ChatEvent

interface EventStore {
    // return generated message events in database
    fun save(event: ChatEvent): Collection<ChatEvent>
}
