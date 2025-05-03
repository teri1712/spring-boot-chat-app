package com.decade.practice.websocket

import com.decade.practice.model.TypeEvent
import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.User
import java.util.*

interface WsEntityRepository {
      fun getUser(username: String): User
      fun getChat(id: ChatIdentifier): Chat
      fun getType(chat: ChatIdentifier, from: UUID, readOnly: Boolean = true): TypeEvent?
}

fun WsEntityRepository.getType(chat: Chat, from: User, readOnly: Boolean = true): TypeEvent? =
      getType(chat.identifier, from.id, readOnly)
