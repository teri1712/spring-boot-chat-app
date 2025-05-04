package com.decade.practice.security.jwt

import com.decade.practice.model.domain.entity.User
import com.decade.practice.security.model.UserClaims
import org.springframework.security.core.AuthenticatedPrincipal
import java.io.Serializable
import java.util.*

class JwtUser(
      val id: UUID,
      val _username: String,
) : AuthenticatedPrincipal, Serializable {

      constructor(user: User) : this(
            user.id,
            user.username,
      )

      constructor(userClaims: UserClaims) : this(
            userClaims.id,
            userClaims.username,
      )

      override fun getName(): String = _username
      override fun hashCode(): Int = Objects.hash(_username)


      override fun equals(other: Any?): Boolean {
            return if (other == null) false
            else other.hashCode() == hashCode()
      }
}
