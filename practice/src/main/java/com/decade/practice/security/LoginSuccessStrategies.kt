package com.decade.practice.security

import com.decade.practice.core.ChatOperations
import com.decade.practice.core.UserOperations
import com.decade.practice.model.local.AccountEntry
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class LoginSuccessStrategy(
      private val userOperations: UserOperations,
      private val chatOperations: ChatOperations,
) : AuthenticationSuccessHandler {

      @Throws(IOException::class)
      override fun onAuthenticationSuccess(
            httpRequest: HttpServletRequest,
            httpResponse: HttpServletResponse,
            authentication: Authentication
      ) {
            onAuthenticationSuccess(httpResponse, authentication)
      }

      @Throws(IOException::class)
      fun onAuthenticationSuccess(
            httpResponse: HttpServletResponse,
            authentication: Authentication,
      ) {
            val principal = authentication.principal
            if (principal !is UserDetails) {
                  throw AccessDeniedException("Operation not supported")
            }
            val account = userOperations.prepareAccount(principal)
            val user = account.user
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
class Oauth2LoginSuccessStrategy(
      @Value("\${front-end}")
      private val frontEnd: String
) : AuthenticationSuccessHandler {

      override fun onAuthenticationSuccess(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authentication: Authentication
      ) {
            response.sendRedirect(frontEnd)
      }
}