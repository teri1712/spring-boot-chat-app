package com.decade.practice.security.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize


@JsonDeserialize
@JsonSerialize
data class TokenCredential(
      var accessToken: String,
      var refreshToken: String,
      var expiresIn: Long,
      var createdAt: Long
)
