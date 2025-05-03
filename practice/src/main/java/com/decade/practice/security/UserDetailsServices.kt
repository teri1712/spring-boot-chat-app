package com.decade.practice.security

import com.decade.practice.core.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.createOauth2User
import com.decade.practice.security.model.DaoUser
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class DaoUserDetailsService(private val userRepo: UserRepository) : UserDetailsService {

      @Throws(UsernameNotFoundException::class)
      override fun loadUserByUsername(username: String): UserDetails {
            try {
                  return DaoUser(userRepo.getByUsername(username))
            } catch (e: Exception) {
                  throw UsernameNotFoundException("Credential with Username: $username does not exist.")
            }
      }
}

class SaveOnLoadOauth2UserService<R : OAuth2UserRequest, U : OAuth2User>(
      private val userOperations: UserOperations,
      private val delegate: OAuth2UserService<R, U>
) : OAuth2UserService<R, U> {
      override fun loadUser(userRequest: R?): U {
            val oAuth2User = delegate.loadUser(userRequest)
            val username = oAuth2User.name
            val name = oAuth2User.attributes["name"].toString()
            val picture = oAuth2User.attributes["picture"].toString()
            try {
                  userOperations.createOauth2User(username, name, picture)
            } catch (ignored: DataIntegrityViolationException) {
            }
            return oAuth2User
      }
}