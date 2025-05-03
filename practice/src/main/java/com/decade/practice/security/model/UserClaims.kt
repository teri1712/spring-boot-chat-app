package com.decade.practice.security.model

import com.decade.practice.model.entity.User
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.util.*

@JsonDeserialize
data class UserClaims(
      val id: UUID,
      val username: String,
      val name: String,
      val role: String,
      val gender: String,
) {
      constructor(user: User) : this(
            user.id,
            user.username,
            user.name,
            user.role,
            user.gender.random(),
      )
}