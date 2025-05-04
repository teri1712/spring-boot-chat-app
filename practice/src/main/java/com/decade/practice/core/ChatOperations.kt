package com.decade.practice.core

import com.decade.practice.model.domain.ChatSnapshot
import com.decade.practice.model.domain.embeddable.ChatIdentifier
import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.User
import java.util.*

private const val CONVERSATION_LIMIT: Int = 20

interface ChatOperations {

      @Throws(NoSuchElementException::class)
      fun getOrCreateChat(identifier: ChatIdentifier): Chat

      fun getOrCreateChat(
            owner: UUID,
            partner: UUID
      ) = getOrCreateChat(ChatIdentifier.from(owner, partner))

      fun listChat(
            owner: User,
            version: Int? = null,
            offset: Chat? = null,
            limit: Int = CONVERSATION_LIMIT
      ): List<Chat>


      fun getSnapshot(
            chat: Chat,
            owner: User,
            atVersion: Int
      ): ChatSnapshot
}
