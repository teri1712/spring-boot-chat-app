package com.decade.practice.util

import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.User
import java.util.*

fun Chat.inspectOwner(me: UUID): User {
    return if (identifier.firstUser == me) firstUser else secondUser
}

fun Chat.inspectOwner(me: User): User {
    return if (firstUser == me) firstUser else secondUser
}

fun Chat.inspectOwner(username: String): User {
    return if (firstUser.username == username)
        firstUser
    else
        secondUser
}

// TODO: convert inspections to extension methods
fun Chat.inspectPartner(me: User): User {
    return if (firstUser == me) secondUser else firstUser
}

fun Chat.inspectPartner(me: UUID): User {
    return if (identifier.firstUser == me) secondUser else firstUser
}

fun toIdentifier(u1: UUID, u2: UUID): ChatIdentifier {
    return if (u1 < u2) ChatIdentifier(u1, u2) else ChatIdentifier(u2, u1)
}