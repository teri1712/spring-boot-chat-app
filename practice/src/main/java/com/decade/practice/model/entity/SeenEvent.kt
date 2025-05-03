package com.decade.practice.model.entity

import com.fasterxml.jackson.annotation.JsonGetter
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.validation.constraints.NotNull

const val SEEN = "SEEN"

@Entity
@DiscriminatorValue(SEEN)
class SeenEvent(
      chat: Chat,
      sender: User,

      @field:NotNull
      @Column(updatable = false)
      val at: Long
) : ChatEvent(chat, sender, SEEN) {
      constructor(event: SeenEvent) : this(event.chat, event.sender, event.at)

      override fun copy(): ChatEvent {
            return SeenEvent(this)
      }

      @get:JsonGetter
      val seenEvent
            get() = com.decade.practice.model.local.SeenEvent(at)
}
