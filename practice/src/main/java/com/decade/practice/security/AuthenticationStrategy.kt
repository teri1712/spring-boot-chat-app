package com.decade.practice.security

import com.decade.practice.database.ChatOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.model.entity.User
import com.decade.practice.model.local.Account
import com.decade.practice.model.local.AccountEntry
import com.decade.practice.security.model.JwtUser
import com.decade.practice.util.CredentialService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.*

@Service
class LoginSuccessStrategy(
    private val userRepo: UserRepository,
    private val chatOperations: ChatOperations,
    private val credentialService: CredentialService<JwtUser>
) : AuthenticationSuccessHandler {

    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        authentication: Authentication
    ) {
        val user: User = userRepo.getByUsername(authentication.name)
        onAuthenticationSuccess(httpRequest, httpResponse, user)
    }

    @Throws(IOException::class)
    fun onAuthenticationSuccess(
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse,
        user: User
    ) {
        val credential = credentialService.create(user)
        val account = Account(user, credential)

        val syncContext = user.syncContext
        val chatList = chatOperations.listChat(user)
            .map { chat -> chatOperations.getSnapshot(chat, user, syncContext.eventVersion) }

        httpResponse.contentType = MediaType.APPLICATION_JSON_VALUE
        httpResponse.writer.write(
            ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(AccountEntry(account, chatList))
        )
        httpResponse.writer.flush()
    }
}


@Service
class LogoutStrategy : LogoutHandler {
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
    }
}