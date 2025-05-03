package com.decade.practice.security

import com.decade.practice.model.entity.User
import com.decade.practice.security.model.TokenCredential
import com.decade.practice.security.model.UserClaims
import org.springframework.security.access.AccessDeniedException

interface TokenCredentialService {

      @Throws(AccessDeniedException::class)
      fun validate(refreshToken: String)
      fun evict(username: String): List<String>
      fun add(username: String, refreshToken: String)

      fun decodeToken(token: String): UserClaims
      fun create(claims: UserClaims, refreshToken: String? = null): TokenCredential
      fun create(user: User, refreshToken: String? = null): TokenCredential
      fun get(username: String): List<String>
}