package com.decade.practice.database.transaction

import com.decade.practice.core.ChatOperations
import com.decade.practice.core.EventStore
import com.decade.practice.model.entity.ChatEvent
import com.decade.practice.model.entity.isMessage
import com.decade.practice.util.inspectOwner
import com.decade.practice.util.inspectPartner
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import jakarta.persistence.PersistenceContext
import org.hibernate.exception.ConstraintViolationException
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Component
@Primary
class ChatEventStore(
      private val eventStore: UserEventStore,
      private val chatOperations: ChatOperations,
) : EventStore {

      @PersistenceContext
      lateinit var em: EntityManager

      @Transactional(isolation = Isolation.READ_COMMITTED)
      @Throws(
            NoSuchElementException::class,
            ConstraintViolationException::class
      )
      override fun save(event: ChatEvent): Collection<ChatEvent> {
            val chat = chatOperations.getOrCreateChat(event.chatIdentifier)
            em.lock(chat, LockModeType.PESSIMISTIC_WRITE)
            if (event.isMessage()) {
                  chat.messageCount++
            }
            event.chat = chat

            val me = chat.inspectOwner(event.sender)
            val you = chat.inspectPartner(me)

            val mine = event.copy().apply {
                  localId = event.localId
            }
            val yours = event.copy()

            mine.owner = me
            yours.owner = you

            return eventStore.save(mine) + eventStore.save(yours)
      }

}
