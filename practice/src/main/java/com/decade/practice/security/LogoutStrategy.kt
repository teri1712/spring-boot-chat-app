package com.decade.practice.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service

@Service
class LogoutStrategy : LogoutHandler {
      override fun logout(
            request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
      ) {

      }
}
