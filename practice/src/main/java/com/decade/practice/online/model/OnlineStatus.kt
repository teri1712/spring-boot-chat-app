package com.decade.practice.online.model

import com.decade.practice.model.domain.entity.User
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.redis.core.RedisHash
import java.time.Instant

private const val FIVE_MINUTES = 5 * 60L
private const val KEYSPACE = "ONLINE"

@JsonDeserialize
@RedisHash(KEYSPACE, timeToLive = FIVE_MINUTES)
data class OnlineStatus(
      @Id
      val username: String,
      val at: Long = Instant.now().epochSecond
) {
      @Transient
      var user: User? = null

      constructor(user: User, at: Long = System.currentTimeMillis()) : this(user.username, at) {
            this.user = user
      }
}