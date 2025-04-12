package com.decade.practice.endpoints.chat

import com.decade.practice.database.ChatOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.repository.get
import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.local.ChatSnapshot
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/chat")
class ChatController(
    private val userRepo: UserRepository,
    private val chatOperations: ChatOperations,
) {

    @GetMapping("/snapshot")
    fun get(
        @AuthenticationPrincipal(expression = "id") id: UUID,
        @RequestParam @Validated identifier: ChatIdentifier,
        @RequestParam(required = false) atVersion: Int?
    ): ResponseEntity<ChatSnapshot> {

        val cacheControl = CacheControl.maxAge(30, TimeUnit.DAYS)
            .cachePublic()

        val me = userRepo.get(id)
        val chat = chatOperations.getOrCreateChat(identifier)
        val snapshot = chatOperations.getSnapshot(chat, me, atVersion ?: me.syncContext.eventVersion)

        return ResponseEntity.ok().cacheControl(cacheControl).body(snapshot)
    }

    @GetMapping
    fun list(
        @AuthenticationPrincipal(expression = "id") id: UUID,
        @RequestParam @Validated startAt: ChatIdentifier,
        @RequestParam atVersion: Int,
    ): ResponseEntity<List<ChatSnapshot>> {
        val owner = userRepo.get(id)
        val cacheControl = CacheControl.maxAge(30, TimeUnit.DAYS)
            .cachePublic()
        val chatList = chatOperations.listChat(
            owner, atVersion, startAt
        )
            .map { chat -> chatOperations.getSnapshot(chat, owner, atVersion) }
        return ResponseEntity.ok().cacheControl(cacheControl).body(chatList)
    }
}
