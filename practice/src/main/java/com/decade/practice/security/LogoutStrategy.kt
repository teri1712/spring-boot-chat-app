package com.decade.practice.security

import com.decade.practice.util.TokenUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service

@Service
class LogoutStrategy(
      private val credentialService: TokenCredentialService
) : LogoutHandler {

      override fun logout(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authentication: Authentication
      ) {
            val refreshToken = TokenUtils.extractRefreshToken(request)
            if (refreshToken == null) {
                  return
            }
            val username = authentication.name
            credentialService.evict(username, refreshToken)
      }
}
