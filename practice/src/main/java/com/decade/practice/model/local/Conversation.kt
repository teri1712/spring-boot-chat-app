package com.decade.practice.model.local

import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.User
import com.decade.practice.utils.inspectPartner
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
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