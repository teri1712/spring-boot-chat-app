package com.decade.practice.model.domain.embeddable

import com.decade.practice.model.domain.entity.User
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*


@Embeddable
data class ChatIdentifier(
      var firstUser: UUID,
      var secondUser: UUID
) : Serializable {
      override fun toString(): String = "$firstUser+$secondUser"

      companion object {
            fun from(u1: UUID, u2: UUID): ChatIdentifier {
                  return if (u1 < u2)
                        ChatIdentifier(u1, u2)
                  else ChatIdentifier(u2, u1)
            }

            fun from(u1: User, u2: User): ChatIdentifier {
                  return from(u1.id, u2.id)
            }
      }
}
