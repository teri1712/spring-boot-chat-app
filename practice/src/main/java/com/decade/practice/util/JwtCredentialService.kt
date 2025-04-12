package com.decade.practice.util

import com.decade.practice.model.entity.User
import com.decade.practice.security.model.Credential
import com.decade.practice.security.model.JwtUser
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

interface CredentialService<Claims> {
    fun decodeToken(token: String): Claims
    fun create(claims: Claims, refreshToken: String? = null): Credential
    fun create(user: User, refreshToken: String? = null): Credential
}

private const val ONE_WEEK = 7L * 24 * 60 * 60 * 1000L
private const val TEN_HOURS = 10L * 60 * 60 * 1000L

@Service
class JwtCredentialService(
    @Value("\${credential.jwt.secret}")
    private val secret: String,
) : CredentialService<JwtUser> {
    private val key: String = Base64.getEncoder().encodeToString(secret.toByteArray())

    @Throws(
        ExpiredJwtException::class,
        UnsupportedJwtException::class,
        MalformedJwtException::class,
        SignatureException::class,
        IllegalArgumentException::class
    )
    override fun decodeToken(token: String): JwtUser {
        val claims = Jwts.parser()
            .setSigningKey(key)
            .parseClaimsJws(token)
            .body
        val id = claims.get("id", String::class.java)
        val username = claims.get("username", String::class.java)
        val name = claims.get("name", String::class.java)
        val passwordVersion = claims.get("password_version", Int::class.javaObjectType)
        return JwtUser(UUID.fromString(id), username, name, passwordVersion)
    }

    private fun encodeToken(user: JwtUser, expiration: Long, at: Long): String {
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setHeaderParam("alg", "HS256")
            .claim("username", user.getUsername())
            .claim("password_version", user.getPasswordVersion())
            .claim("id", user.id)
            .claim("name", user.fullName)
            .setIssuedAt(Date(at))
            .setExpiration(Date(at + expiration))
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()
    }

    override fun create(claims: JwtUser, refreshToken: String?): Credential {
        var refresh = refreshToken
        val currentTime = System.currentTimeMillis()

        val accessToken = encodeToken(claims, ONE_WEEK, currentTime)
        if (refresh == null) {
            refresh = encodeToken(claims, TEN_HOURS, currentTime)
        }

        return Credential(
            accessToken = accessToken,
            refreshToken = refresh,
            expiresIn = 10L * 60 * 60 * 1000,
            createdAt = currentTime
        )
    }


    override fun create(user: User, refreshToken: String?): Credential {
        val claims = JwtUser(
            id = user.id,
            username = user.username,
            passwordVersion = user.passwordVersion,
            fullName = user.name
        )
        return create(claims, refreshToken)
    }
}