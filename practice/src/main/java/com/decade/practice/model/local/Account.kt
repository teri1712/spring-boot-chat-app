package com.decade.practice.model.local

import com.decade.practice.model.SyncContext
import com.decade.practice.model.entity.User
import com.decade.practice.security.model.Credential
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.*


@JsonDeserialize
@JsonSerialize
data class Account(
    val id: UUID,
    val username: String,
    val user: User,
    val credential: Credential,
    val syncContext: SyncContext,
) {
    constructor(user: User, credential: Credential) : this(
        user.id,
        user.username,
        user,
        credential,
        user.syncContext
    )
}
