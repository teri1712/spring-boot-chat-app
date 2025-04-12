package com.decade.practice.database

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
        version: Int,
        offset: Chat,
        limit: Int = CONVERSATION_LIMIT
    ): List<Chat>

    fun listChat(
        owner: User,
        version: Int,
        offset: ChatIdentifier,
        limit: Int = CONVERSATION_LIMIT
    ): List<Chat>

    fun listChat(
        owner: User,
        version: Int
    ): List<Chat>

    fun listChat(owner: User): List<Chat>

    fun getSnapshot(
        chat: Chat,
        owner: User,
        atVersion: Int
    ): ChatSnapshot
}
