package com.decade.practice.security.jwt

import com.decade.practice.util.TokenUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Service
class JwtTokenFilter(
      private val jwtCredentialService: JwtCredentialService,
) : OncePerRequestFilter() {

      @Throws(ServletException::class, IOException::class)
      override fun doFilterInternal(
            request: HttpServletRequest, response: HttpServletResponse,
            filterChain: FilterChain
      ) {
            if (SecurityContextHolder.getContext().authentication != null) {
                  filterChain.doFilter(request, response)
                  return
            }
            try {
                  val accessToken = TokenUtils.extractToken(request)
                  if (accessToken != null) {
                        val claims = jwtCredentialService.decodeToken(accessToken)
                        val principal = JwtUser(claims)
                        val context = SecurityContextHolder.createEmptyContext()
                        val authentication = JwtUserAuthentication(principal, accessToken)
                        context.authentication = authentication
                        SecurityContextHolder.setContext(context)
                        // NOTE: For token-based authentication, will not be saved into security context repository
                  }
                  filterChain.doFilter(request, response)

            } catch (e: Exception) {
                  e.printStackTrace()
                  response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
            }
      }

}