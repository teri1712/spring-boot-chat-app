package com.decade.practice.security.jwt

import com.decade.practice.database.UserOperations
import com.decade.practice.security.model.JwtUser
import com.decade.practice.security.model.JwtUserAuthentication
import com.decade.practice.util.JwtCredentialService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


const val REFRESH_PATH = "/authentication/refresh-token"
const val REFRESH_PARAM: String = "refresh_token"
const val HEADER_NAME: String = "Authorization"
const val BEARER: String = "Bearer "

@Service
class JwtAuthenticationFilter(
    private val userOperations: UserOperations,
    private val contextRepo: SecurityContextRepository,
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
            if (REFRESH_MATCHER.matches(request)) {
                val refreshToken = request.getParameter(REFRESH_PARAM)
                    ?: throw JwtException("NO TOKEN REPRESENTED")
                val claims = jwtCredentialService.decodeToken(refreshToken)
                userOperations.validateCredential(claims)

                val credential = jwtCredentialService.create(claims, refreshToken)
                onAuthenticatedToken(request, response, claims, credential.accessToken)

                response.contentType = MediaType.APPLICATION_JSON_VALUE
                response.characterEncoding = Charsets.UTF_8.name()
                response.writer.write(
                    ObjectMapper()
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .writeValueAsString(credential)
                )
                return
            }

            val accessToken = extractToken(request)
            if (accessToken != null) {
                val claims = jwtCredentialService.decodeToken(accessToken)
                onAuthenticatedToken(request, response, claims, accessToken)
            }
            filterChain.doFilter(request, response)

        } catch (e: Exception) {
            e.printStackTrace()
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.message)
        }
    }

    private fun onAuthenticatedToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
        claims: JwtUser,
        accessToken: String
    ) {
        val context = SecurityContextHolder.createEmptyContext()
        val authentication = JwtUserAuthentication(
            claims,
            accessToken
        )
        context.authentication = authentication
        SecurityContextHolder.setContext(context)
        contextRepo.saveContext(context, request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val headers = request.getHeaders(HEADER_NAME)
        for (header in headers) {
            if (header.startsWith(BEARER)) {
                return header.substring(BEARER.length)
            }
        }

        return null
    }

    companion object {
        private val REFRESH_MATCHER = AntPathRequestMatcher(
            REFRESH_PATH,
            "POST"
        )
    }
}