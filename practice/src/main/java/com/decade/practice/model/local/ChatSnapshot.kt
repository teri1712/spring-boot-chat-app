package com.decade.practice.model.local

import com.decade.practice.model.entity.ChatEvent


data class ChatSnapshot(
    val conversation: Conversation,
    val eventList: List<ChatEvent>,
    val atVersion: Int,
)
