package com.decade.practice.database.transaction

import com.decade.practice.core.ChatOperations
import com.decade.practice.core.common.SelfAware
import com.decade.practice.database.repository.*
import com.decade.practice.model.domain.ChatSnapshot
import com.decade.practice.model.domain.embeddable.ChatIdentifier
import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.User
import com.decade.practice.model.local.Conversation
import com.decade.practice.util.EventPageUtils
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.exception.ConstraintViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
      private val userRepo: UserRepository,
      private val eventRepo: EventRepository,
      private val edgeRepo: EdgeRepository,
      private val chatRepo: ChatRepository,
) : SelfAware(), ChatOperations {

      @PersistenceContext
      private lateinit var em: EntityManager

      override fun getOrCreateChat(identifier: ChatIdentifier): Chat {
            try {
                  return chatRepo.get(identifier)
            } catch (e: NoSuchElementException) {
                  ensureExists(identifier)
            }
            return chatRepo.get(identifier)
      }

      private fun ensureExists(chatIdentifier: ChatIdentifier) {
            try {
                  (self as ChatService).createChat(chatIdentifier)
            } catch (ignored: ConstraintViolationException) {
                  println(ignored)
            }
      }

      @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = [NoSuchElementException::class]
      )
      @Throws(NoSuchElementException::class)
      fun createChat(identifier: ChatIdentifier) {
            val chat = Chat(
                  userRepo.get(identifier.firstUser),
                  userRepo.get(identifier.secondUser)
            )
            em.persist(chat)
            em.flush()
      }

      @Transactional(isolation = Isolation.READ_COMMITTED)
      override fun listChat(
            owner: User,
            version: Int?,
            offset: Chat?,
            limit: Int
      ): List<Chat> {
            val version = version ?: owner.syncContext.eventVersion
            var currentChat = offset ?: edgeRepo.getHeadEdge(owner, version)?.from ?: return emptyList()
            var count = limit
            val chatList = mutableListOf(currentChat)
            while (--count >= 0) {
                  val next = edgeRepo.getEdgeFrom(owner, currentChat, version)?.dest
                  if (next != null) {
                        currentChat = next
                        chatList.add(currentChat)
                  } else {
                        break
                  }
            }
            return chatList
      }

      override fun getSnapshot(
            chat: Chat,
            owner: User,
            atVersion: Int
      ): ChatSnapshot {
            val page = EventPageUtils.pageEvent
            val eventList = eventRepo.findByOwnerAndChatAndEventVersionLessThanEqual(owner, chat, atVersion, page)
            return ChatSnapshot(
                  conversation = Conversation(chat, owner),
                  eventList = eventList,
                  atVersion
            )
      }

}

