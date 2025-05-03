package com.decade.practice.security.model

import com.decade.practice.model.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class DaoUser(user: User) : UserDetails {

      val id = user.id
      private val _password: String = user.password
      private val _username: String = user.username
      private val _role: String = user.role

      override fun getAuthorities(): Collection<GrantedAuthority?> {
            return listOf(SimpleGrantedAuthority(_role))
      }

      override fun getPassword(): String {
            return _password
      }

      override fun getUsername(): String {
            return _username
      }

      override fun hashCode(): Int {
            return Objects.hash(username)
      }

      override fun equals(other: Any?): Boolean {
            return if (other == null) false
            else other.hashCode() == hashCode()
      }
}


