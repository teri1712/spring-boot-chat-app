package com.decade.practice.websocket.arguments

import com.decade.practice.model.domain.entity.User
import com.decade.practice.websocket.WsEntityRepository
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.SimpMessageHeaderAccessor


class UserArgumentResolver(private val entityRepo: WsEntityRepository) : HandlerMethodArgumentResolver {

      override fun supportsParameter(parameter: MethodParameter): Boolean {
            return User::class.java.isAssignableFrom(parameter.parameterType)
      }

      override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any {
            val principal = SimpMessageHeaderAccessor.getUser(message.headers)!!
            return entityRepo.getUser(principal.name)
      }
}