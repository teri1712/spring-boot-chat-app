package com.decade.practice.database.repository

import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.ChatEvent
import com.decade.practice.model.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional(
      readOnly = true,
      isolation = Isolation.READ_COMMITTED,
      propagation = Propagation.REQUIRED
)
interface EventRepository : JpaRepository<ChatEvent, UUID> {

      fun findByOwnerAndChatAndEventVersionLessThanEqual(
            owner: User,
            chat: Chat,
            eventVersion: Int,
            pageable: Pageable
      ): List<ChatEvent>

      fun findByOwnerAndEventVersionLessThanEqual(
            owner: User,
            eventVersion: Int,
            pageable: Pageable
      ): List<ChatEvent>

      fun findFirstByOwnerOrderByEventVersionDesc(
            owner: User
      ): ChatEvent

      fun getByLocalId(localId: UUID): ChatEvent
}
