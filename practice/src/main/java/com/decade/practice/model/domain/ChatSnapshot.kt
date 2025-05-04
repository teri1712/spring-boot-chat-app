package com.decade.practice.model.domain

import com.decade.practice.model.domain.entity.ChatEvent
import com.decade.practice.model.local.Conversation

data class ChatSnapshot(
      val conversation: Conversation,
      val eventList: List<ChatEvent>,
      val atVersion: Int,
)