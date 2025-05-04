package com.decade.practice.endpoints.chat

import com.decade.practice.core.ChatOperations
import com.decade.practice.database.repository.ChatRepository
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.repository.get
import com.decade.practice.model.domain.ChatSnapshot
import com.decade.practice.model.domain.embeddable.ChatIdentifier
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/chat")
class ChatController(
      private val userRepo: UserRepository,
      private val chatRepo: ChatRepository,
      private val chatOperations: ChatOperations,
) {

      @GetMapping("/snapshot")
      fun get(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestParam @Validated identifier: ChatIdentifier,
            @RequestParam(required = false) atVersion: Int?
      ): ResponseEntity<ChatSnapshot> {

            val cacheControl = CacheControl.maxAge(30, TimeUnit.DAYS)
                  .cachePublic()

            val me = userRepo.getByUsername(username)
            val chat = chatOperations.getOrCreateChat(identifier)
            val snapshot = chatOperations.getSnapshot(chat, me, atVersion ?: me.syncContext.eventVersion)
            return ResponseEntity.ok().cacheControl(cacheControl)
                  .varyBy("Cookie", "Authorization")
                  .body(snapshot)
      }

      @GetMapping
      fun list(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestParam(required = false) @Validated startAt: ChatIdentifier?,
            @RequestParam atVersion: Int,
      ): ResponseEntity<List<ChatSnapshot>> {
            val chat = if (startAt == null) null else chatRepo.get(startAt)
            val owner = userRepo.getByUsername(username)
            val cacheControl = CacheControl.maxAge(30, TimeUnit.DAYS)
                  .cachePublic()
            val chatList = chatOperations.listChat(owner, atVersion, chat)
            val snapshotList = chatList.map { chat ->
                  chatOperations.getSnapshot(chat, owner, atVersion)
            }
            return ResponseEntity.ok().cacheControl(cacheControl)
                  .varyBy("Cookie", "Authorization")
                  .body(snapshotList)
      }
}
