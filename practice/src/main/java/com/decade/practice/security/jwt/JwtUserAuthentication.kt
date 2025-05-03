package com.decade.practice.security.jwt

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class JwtUserAuthentication(
    private val claims: JwtUser,
    private val accessToken: String
) : AbstractAuthenticationToken(
    listOf(SimpleGrantedAuthority("ROLE_USER"))
) {
    init {
        isAuthenticated = true
        details = claims
    }

    override fun getCredentials(): Any {
        return accessToken
    }

    override fun getPrincipal(): Any {
        return claims
    }
}
