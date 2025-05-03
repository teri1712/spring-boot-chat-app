package com.decade.practice.core

import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.User
import com.decade.practice.model.local.ChatSnapshot
import com.decade.practice.util.toIdentifier
import java.util.*

private const val CONVERSATION_LIMIT: Int = 20

interface ChatOperations {

      @Throws(NoSuchElementException::class)
      fun getOrCreateChat(identifier: ChatIdentifier): Chat

      fun getOrCreateChat(
            owner: UUID,
            partner: UUID
      ) = getOrCreateChat(toIdentifier(owner, partner))

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
