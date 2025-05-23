package com.decade.practice.websocket

import com.decade.practice.websocket.arguments.ChatArgumentResolver
import com.decade.practice.websocket.arguments.ChatIdentifierArgumentResolver
import com.decade.practice.websocket.arguments.UserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.HandshakeInterceptor


const val HANDSHAKE_DESTINATION = "/handshake"
const val USER_DESTINATION = "/user"
const val MQ_DESTINATION = "/queue/message"
const val MQ_CHAT_DESTINATION = "/queue/chat"
const val TYPING_DESTINATION = "/typing"

const val USER_QUEUE_DESTINATION = "$USER_DESTINATION$MQ_DESTINATION"
val BROKER_DESTINATIONS = hashSetOf(MQ_CHAT_DESTINATION, MQ_DESTINATION)


@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
class WsConfiguration(
      private val entityRepo: WsEntityRepository,
      private val interceptors: List<ChannelInterceptor>,
      private val handShakeInterceptors: List<HandshakeInterceptor>
) : WebSocketMessageBrokerConfigurer {

      override fun registerStompEndpoints(registry: StompEndpointRegistry) {
            registry.addEndpoint(HANDSHAKE_DESTINATION)
                  .addInterceptors(object : HandshakeInterceptor {
                        @Throws(Exception::class)
                        override fun beforeHandshake(
                              request: ServerHttpRequest,
                              response: ServerHttpResponse,
                              wsHandler: WebSocketHandler,
                              attributes: Map<String, Any>
                        ): Boolean {
                              return request.principal != null
                        }

                        override fun afterHandshake(
                              request: ServerHttpRequest,
                              response: ServerHttpResponse,
                              wsHandler: WebSocketHandler,
                              exception: Exception?
                        ) {
                        }
                  }).addInterceptors(*handShakeInterceptors.toTypedArray())
                  .setAllowedOrigins("http://localhost:4200")
            // .withSockJS();
      }

      override fun configureClientInboundChannel(registration: ChannelRegistration) {
            interceptors.forEach {
                  registration.interceptors(it)
            }
      }


      override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
            argumentResolvers.add(ChatIdentifierArgumentResolver())
            argumentResolvers.add(UserArgumentResolver(entityRepo))
            argumentResolvers.add(ChatArgumentResolver(entityRepo))
      }

      override fun configureMessageBroker(registry: MessageBrokerRegistry) {
            registry.enableSimpleBroker(*BROKER_DESTINATIONS.toTypedArray())
            registry.setUserDestinationPrefix(USER_DESTINATION)
      }

}
