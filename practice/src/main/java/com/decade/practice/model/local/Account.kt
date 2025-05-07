package com.decade.practice.model.local

import com.decade.practice.model.TokenCredential
import com.decade.practice.model.domain.SyncContext
import com.decade.practice.model.domain.entity.User
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.*


@JsonDeserialize
@JsonSerialize
data class Account(
      val id: UUID,
      val username: String,
      val user: User,
      val credential: TokenCredential?,
      val syncContext: SyncContext,
) {
      constructor(user: User, credential: TokenCredential?) : this(
            user.id,
            user.username,
            user,
            credential,
            user.syncContext
      )
}
