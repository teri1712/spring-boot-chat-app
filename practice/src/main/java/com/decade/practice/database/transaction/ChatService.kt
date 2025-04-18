package com.decade.practice.database.transaction

import com.decade.practice.database.ChatOperations
import com.decade.practice.database.repository.*
import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.User
import com.decade.practice.model.local.ChatSnapshot
import com.decade.practice.model.local.Conversation
import com.decade.practice.util.EventPageUtils
import com.decade.practice.util.SelfAware
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.hibernate.exception.ConstraintViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

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
        version: Int,
        offset: Chat,
        limit: Int
    ): List<Chat> {
        var currentChat = offset
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun listChat(
        owner: User,
        version: Int,
        offset: ChatIdentifier,
        limit: Int
    ): List<Chat> {
        val current = chatRepo.get(offset)
        return listChat(owner, version, current, limit)
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun listChat(
        owner: User,
        version: Int
    ): List<Chat> {
        val index = edgeRepo.getHeadEdge(owner, version) ?: return emptyList()
        return listChat(owner, version, index.from)
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    override fun listChat(
        owner: User
    ): List<Chat> {
        return listChat(owner, owner.syncContext.eventVersion)
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

