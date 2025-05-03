package com.decade.practice.model.entity

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

const val WELCOME = "HELLO WORLD"

private fun welcomeMessage(user: User): String {
      return "HELLO ${user.username}"
}

@Entity
@DiscriminatorValue(WELCOME)
class WelcomeEvent(
      chat: Chat,
      admin: User,
      user: User,
) : TextEvent(chat, admin, welcomeMessage(user)) {
      init {
            owner = user
      }
}
