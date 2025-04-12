package com.decade.practice.security

import com.decade.practice.database.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.createOauth2User
import com.decade.practice.security.model.DaoUser
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.*

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

@Service
class SaveOnLoadOauth2UserService(
    private val userOperations: UserOperations
) :
    DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val username = oAuth2User.name
        val name = oAuth2User.attributes["name"].toString()
        try {
            userOperations.createOauth2User(username, name)
        } catch (ignored: DataIntegrityViolationException) {
        }
        return oAuth2User
    }
}