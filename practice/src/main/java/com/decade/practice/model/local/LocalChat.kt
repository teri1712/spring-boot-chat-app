package com.decade.practice.model.local

import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.User
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.util.*

@JsonDeserialize
data class LocalChat(
    val identifier: ChatIdentifier,
    val owner: UUID,
    val partner: UUID = if (identifier.firstUser == owner)
        identifier.secondUser else identifier.firstUser
) {
    constructor(chat: Chat, owner: User) : this(chat.identifier, owner.id)
}