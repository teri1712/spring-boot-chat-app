package com.decade.practice.websocket

import com.decade.practice.model.domain.TypeEvent
import com.decade.practice.model.domain.embeddable.ChatIdentifier
import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.User
import java.util.*

interface WsEntityRepository {
      fun getUser(username: String): User
      fun getChat(id: ChatIdentifier): Chat
      fun getType(chat: ChatIdentifier, from: UUID, readOnly: Boolean = true): TypeEvent?
}

fun WsEntityRepository.getType(chat: Chat, from: User, readOnly: Boolean = true): TypeEvent? =
      getType(chat.identifier, from.id, readOnly)
