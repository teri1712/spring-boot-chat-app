package com.decade.practice.security.model

import com.decade.practice.model.entity.User
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.AuthenticatedPrincipal
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable
import java.util.*

class DaoUser(user: User) : UserDetails, CredentialModifierInformation {

    val id = user.id
    private val _password: String = user.password
    private val _username: String = user.username
    private val _passwordVersion: Int = user.passwordVersion

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String {
        return _password
    }

    override fun getUsername(): String {
        return _username
    }

    override fun getPasswordVersion(): Int {
        return _passwordVersion
    }

    override fun hashCode(): Int {
        return Objects.hash(username)
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null) false
        else other.hashCode() == hashCode()
    }
}


class JwtUser(
    val id: UUID,
    username: String,
    val fullName: String,
    passwordVersion: Int,
) : AuthenticatedPrincipal, CredentialModifierInformation, Serializable {
    private val _username = username
    private val _passwordVersion = passwordVersion

    constructor(user: User) : this(
        user.id, user.username, user.name, user.password.hashCode()
    )

    override fun getName(): String = _username
    override fun getPasswordVersion(): Int = _passwordVersion
    override fun getUsername(): String = _username
    override fun hashCode(): Int = Objects.hash(_username)


    override fun equals(other: Any?): Boolean {
        return if (other == null) false
        else other.hashCode() == hashCode()
    }
}


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
