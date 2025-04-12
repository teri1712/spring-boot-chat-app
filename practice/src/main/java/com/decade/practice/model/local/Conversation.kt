package com.decade.practice.model.local

import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.User
import com.decade.practice.util.inspectPartner


data class Conversation(
    val chat: LocalChat,
    val partner: User,
    val owner: User,
) {
    constructor(chat: Chat, owner: User) : this(
        LocalChat(chat, owner),
        chat.inspectPartner(owner),
        owner,
    )
}