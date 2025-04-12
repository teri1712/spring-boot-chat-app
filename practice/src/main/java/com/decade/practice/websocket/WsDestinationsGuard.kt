package com.decade.practice.websocket

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component

@Component
class WsDestinationsGuard : ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val destination = SimpMessageHeaderAccessor.getDestination(message.headers)
            ?: return message

        for (prefix in BROKER_DESTINATIONS)
            if (destination.startsWith(prefix))
                return null

        return message
    }
}
