package com.decade.practice.websocket

import com.decade.practice.endpoints.extractChatIdentifier
import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.User
import org.springframework.core.MethodParameter
import org.springframework.messaging.Message
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.support.NativeMessageHeaderAccessor


const val CHAT_HEADER = "chat_identifier"

fun resolveChatHeader(message: Message<*>): ChatIdentifier {
    val nativeHeaders = message.headers[NativeMessageHeaderAccessor.NATIVE_HEADERS] as? Map<String, List<String>>
        ?: throw IllegalStateException("Message must contain native headers")

    val chatHeaderValue = nativeHeaders[CHAT_HEADER]?.firstOrNull()
        ?: throw IllegalArgumentException("Missing or empty '$CHAT_HEADER' header")

    return chatHeaderValue.extractChatIdentifier()
}

class ChatIdentifierArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == ChatIdentifier::class.java
    }

    override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any {
        return resolveChatHeader(message)
    }
}

class ChatArgumentResolver(private val entityRepo: WsEntityRepository) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return Chat::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any {
        val id = resolveChatHeader(message)
        return entityRepo.getChat(id)
    }
}


class UserArgumentResolver(private val entityRepo: WsEntityRepository) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return User::class.java.isAssignableFrom(parameter.parameterType)
    }

    override fun resolveArgument(parameter: MethodParameter, message: Message<*>): Any {
        val principal = SimpMessageHeaderAccessor.getUser(message.headers)!!
        return entityRepo.getUser(principal.name)
    }
}