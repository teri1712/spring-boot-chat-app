package com.decade.practice.utils

import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.User
import java.util.*

fun Chat.inspectOwner(me: UUID): User {
      return if (identifier.firstUser == me) firstUser else secondUser
}

fun Chat.inspectOwner(me: User): User {
      return if (firstUser == me) firstUser else secondUser
}

fun Chat.inspectOwner(username: String): User {
      return if (firstUser.username == username) firstUser else secondUser
}

// TODO: convert inspections to extension methods
fun Chat.inspectPartner(me: User): User {
      return if (firstUser == me) secondUser else firstUser
}

fun Chat.inspectPartner(me: UUID): User {
      return if (identifier.firstUser == me) secondUser else firstUser
}

