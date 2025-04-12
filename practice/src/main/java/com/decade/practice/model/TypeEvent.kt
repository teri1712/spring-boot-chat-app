package com.decade.practice.model

import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.User
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.*

fun determineKey(from: UUID, chat: ChatIdentifier): String {
    val partner = if (from == chat.firstUser)
        chat.secondUser else chat.firstUser
    return "$from->$partner"
}


@JsonDeserialize
@JsonSerialize
data class TypeEvent(
    val from: UUID,
    val chat: ChatIdentifier,
    val time: Long = System.currentTimeMillis(),
) {
    val key: String = determineKey(from, chat)

    constructor(owner: User, chat: Chat) : this(owner.id, chat.identifier)
}
