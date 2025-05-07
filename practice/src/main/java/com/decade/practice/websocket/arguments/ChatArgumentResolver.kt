package com.decade.practice.websocket.arguments

import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.websocket.WsEntityRepository
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver


class ChatArgumentResolver(private val entityRepo: WsEntityRepository) : HandlerMethodArgumentResolver {

      override fun supportsParameter(parameter: MethodParameter): Boolean {
            return Chat::class.java.isAssignableFrom(parameter.parameterType)
      }

      override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any {
            val id = resolveChatHeader(message)
            return entityRepo.getChat(id)
      }
}