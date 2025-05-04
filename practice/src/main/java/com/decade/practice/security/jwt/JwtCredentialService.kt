package com.decade.practice.security.jwt

import com.decade.practice.model.domain.entity.User
import com.decade.practice.security.TokenCredentialService
import com.decade.practice.security.model.TokenCredential
import com.decade.practice.security.model.UserClaims
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit


private const val ONE_WEEK = 7L * 24 * 60 * 60 * 1000L
private const val ONE_MONTH = 30 * 24 * 60 * 60 * 1000L
private const val TOKEN_KEY_SPACE = "JWT_TOKENS"

@Service
class JwtCredentialService(
      @Value("\${credential.jwt.secret}")
      private val secret: String,
      private val redisTemplate: StringRedisTemplate
) : TokenCredentialService {

      private val key: String = Base64.getEncoder().encodeToString(secret.toByteArray())
      private val objectMapper = ObjectMapper()

      private fun generateKey(username: String): String {
            return "$TOKEN_KEY_SPACE:$username"
      }

      override fun add(username: String, refreshToken: String) {
            val key = generateKey(username)
            redisTemplate.opsForSet().add(key, refreshToken)
            redisTemplate.expire(key, ONE_MONTH, TimeUnit.MILLISECONDS)
      }

      @Throws(AccessDeniedException::class)
      override fun validate(refreshToken: String) {
            val claims: UserClaims
            try {
                  claims = decodeToken(refreshToken)
            } catch (e: Exception) {
                  throw AccessDeniedException("Token expired", e)
            }
            val key = generateKey(claims.username)
            val activeTokens = redisTemplate.opsForSet().members(key)
            if (activeTokens?.contains(refreshToken) ?: true) {
                  throw AccessDeniedException("Token expired")
            }
      }

      override fun evict(username: String): List<String> {
            val key = generateKey(username)
            val deletedTokens = get(username)
            deletedTokens.forEach { value ->
                  redisTemplate.opsForSet().remove(key, value)
            }
            return deletedTokens
      }

      override fun evict(username: String, refreshToken: String) {
            val key = generateKey(username)
            redisTemplate.opsForSet().remove(key, refreshToken)
      }

      override fun get(username: String): List<String> {
            val key = generateKey(username)
            return redisTemplate.opsForSet().members(key)?.map {
                  it.toString()
            } ?: emptyList()
      }


      @Throws(
            ExpiredJwtException::class,
            UnsupportedJwtException::class,
            MalformedJwtException::class,
            SignatureException::class,
            IllegalArgumentException::class
      )
      private fun parse(token: String) = Jwts.parser()
            .setSigningKey(key)
            .parseClaimsJws(token)

      @Throws(
            ExpiredJwtException::class,
            UnsupportedJwtException::class,
            MalformedJwtException::class,
            SignatureException::class,
            IllegalArgumentException::class
      )
      override fun decodeToken(token: String): UserClaims {
            val claims = parse(token).body
            return objectMapper.convertValue(claims, UserClaims::class.java)
      }

      private fun encodeToken(user: UserClaims, expiration: Long, at: Long): String {
            val claims: Map<String, Any> = objectMapper.convertValue(
                  user,
                  object : TypeReference<Map<String, Any>>() {})
            return Jwts.builder()
                  .setHeaderParam("typ", "JWT")
                  .setHeaderParam("alg", "HS256")
                  .setClaims(claims)
                  .setIssuedAt(Date(at))
                  .setExpiration(Date(at + expiration))
                  .signWith(SignatureAlgorithm.HS256, key)
                  .compact()
      }

      override fun create(claims: UserClaims, refreshToken: String?): TokenCredential {
            var refresh = refreshToken
            val currentTime = System.currentTimeMillis()

            val accessToken = encodeToken(claims, ONE_WEEK, currentTime)
            if (refresh == null) {
                  refresh = encodeToken(claims, ONE_MONTH, currentTime)
                  add(claims.username, refresh)
            }

            return TokenCredential(
                  accessToken = accessToken,
                  refreshToken = refresh,
                  expiresIn = ONE_WEEK,
                  createdAt = currentTime
            )
      }

      override fun create(user: User, refreshToken: String?): TokenCredential {
            val claims = UserClaims(user)
            return create(claims, refreshToken)
      }
}