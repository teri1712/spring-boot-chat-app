package com.decade.practice.websocket

import com.decade.practice.database.repository.EventRepository
import com.decade.practice.model.domain.TypeEvent
import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.User
import com.decade.practice.util.inspectPartner
import org.springframework.messaging.Message
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.util.MimeTypeUtils


@Controller
class MessageController(
      private val brokerTemplate: SimpMessagingTemplate,
      private val eventRepo: EventRepository,
      private val entityRepo: WsEntityRepository,
) {

      @SubscribeMapping(USER_QUEUE_DESTINATION)
      fun subsSelf(user: User) =
            eventRepo.findFirstByOwnerOrderByEventVersionDesc(user)


      private fun Chat.resolveDestination() =
            "$MQ_CHAT_DESTINATION-${identifier}"


      @MessageMapping(TYPING_DESTINATION)
      fun pingType(
            chat: Chat,
            from: User,
            message: Message<*>
      ) {
            val accessor = SimpMessageHeaderAccessor.wrap(message)
            accessor.setContentType(MimeTypeUtils.APPLICATION_JSON)

            val type = entityRepo.getType(chat, from, false)
            brokerTemplate.convertAndSend(chat.resolveDestination(), type!!, accessor.messageHeaders)
      }

      @SubscribeMapping(TYPING_DESTINATION)
      fun subsType(
            chat: Chat,
            from: User,
            message: Message<*>
      ): TypeEvent? {
            brokerTemplate.send(chat.resolveDestination(), message)
            return entityRepo.getType(chat, chat.inspectPartner(from))
      }

}
