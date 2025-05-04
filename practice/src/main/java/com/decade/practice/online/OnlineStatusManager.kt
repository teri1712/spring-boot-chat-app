package com.decade.practice.online

import com.decade.practice.model.domain.entity.User
import com.decade.practice.online.model.OnlineStatus
import com.decade.practice.websocket.WsEntityRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.time.Instant

private const val KEYSPACE = "ONLINE_USERS"
private const val TTL = 5 * 60

@Configuration
class RedisRepositoriesConfig {

      @Bean
      fun redisTemplate(connFactory: RedisConnectionFactory) = RedisTemplate<Any, Any>()
            .apply {
                  connectionFactory = connFactory
                  setDefaultSerializer(GenericJackson2JsonRedisSerializer())
            }
}

@Component
class OnlineStatusManager(
      private val onlineRepo: OnlineRepository,
      private val entityRepo: WsEntityRepository,
      redisTemplate: RedisTemplate<Any, Any>
) : OnlineStatistic, ChannelInterceptor, HandshakeInterceptor {

      private val zSet = redisTemplate.opsForZSet()

      private fun evict() {
            zSet.removeRangeByScore(
                  KEYSPACE, 0.0, Instant.now().epochSecond.toDouble() - TTL
            )
      }

      private fun set(user: User, at: Long): OnlineStatus {
            evict()
            val status = onlineRepo.save(OnlineStatus(user, at))
            zSet.add(KEYSPACE, status.username, status.at.toDouble())
            return status
      }

      override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
            val principal = SimpMessageHeaderAccessor.getUser(message.headers)
                  ?: return message
            set(entityRepo.getUser(principal.name), Instant.now().epochSecond)
            return message
      }

      override fun beforeHandshake(
            request: ServerHttpRequest, response: ServerHttpResponse,
            wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>
      ): Boolean = true

      override fun afterHandshake(
            request: ServerHttpRequest, response: ServerHttpResponse,
            wsHandler: WebSocketHandler, exception: Exception?
      ) {
            val username = request.principal!!.name
            set(entityRepo.getUser(username), Instant.now().epochSecond)
      }

      override fun get(username: String): OnlineStatus =
            onlineRepo.findById(username).orElse(OnlineStatus(username, 0))
                  .apply { user = entityRepo.getUser(username) }

      override fun getOnlineList(username: String): List<OnlineStatus> {
            evict()
            val result = (zSet.rangeWithScores(KEYSPACE, 0, -1)?.map {
                  val who = it.value as String
                  val at = it.score!!.toLong()
                  OnlineStatus(who, at).apply { user = entityRepo.getUser(who) }
            } ?: emptyList())

            return result.filter { it.username != username }
      }

}