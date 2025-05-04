package com.decade.practice.database.repository

import com.decade.practice.model.domain.embeddable.ChatIdentifier
import com.decade.practice.model.domain.entity.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface ChatRepository : JpaRepository<Chat, ChatIdentifier> {

      @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
      override fun findById(chatIdentifier: ChatIdentifier): Optional<Chat>
}
