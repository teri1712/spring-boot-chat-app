package com.decade.practice.controllers.rest

import com.decade.practice.core.ChatOperations
import com.decade.practice.database.repository.ChatRepository
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.repository.get
import com.decade.practice.model.domain.ChatSnapshot
import com.decade.practice.model.domain.embeddable.ChatIdentifier
import com.decade.practice.utils.CacheUtils.defaultCacheControl
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller responsible for handling chat-related HTTP requests.
 * Provides endpoints for retrieving chat snapshots and chat lists.
 * Mapped to the "/chat" base URL path.
 */
@RestController
@RequestMapping("/chat")
class ChatController(
      private val userRepo: UserRepository,
      private val chatRepo: ChatRepository,
      private val chatOperations: ChatOperations,
) {

      /**
       * Retrieves a single chat snapshot based on the provided identifier.
       *
       * @param username The authenticated user's username extracted from the security principal
       * @param identifier The unique identifier for the chat, consisting of two user IDs
       * @param atVersion Optional version parameter to retrieve a specific chat snapshot version
       * @return ResponseEntity containing the chat snapshot with caching headers
       */
      @GetMapping("/snapshot")
      fun get(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestParam @Validated identifier: ChatIdentifier,
            @RequestParam(required = false) atVersion: Int?
      ): ResponseEntity<ChatSnapshot> {

            // Configure cache control to improve performance
            val cacheControl = defaultCacheControl
            // Retrieve the authenticated user
            val me = userRepo.getByUsername(username)
            // Get or create the chat based on the identifier
            val chat = chatOperations.getOrCreateChat(identifier)
            // Generate a snapshot at the specified version or use the user's current sync version
            val snapshot = chatOperations.getSnapshot(chat, me, atVersion ?: me.syncContext.eventVersion)

            // Return the snapshot with appropriate cache headers
            return ResponseEntity.ok().cacheControl(cacheControl)
                  .varyBy("Cookie", "Authorization")
                  .body(snapshot)
      }

      /**
       * Retrieves a list of chat snapshots for the authenticated user.
       *
       * @param username The authenticated user's username extracted from the security principal
       * @param startAt Optional chat identifier to start the list from (for pagination)
       * @param atVersion The version of chat snapshots to retrieve
       * @return ResponseEntity containing a list of chat snapshots with caching headers
       */
      @GetMapping
      fun list(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestParam(required = false) @Validated startAt: ChatIdentifier?,
            @RequestParam atVersion: Int,
      ): ResponseEntity<List<ChatSnapshot>> {
            // Get the starting chat if startAt is provided, otherwise use null
            val chat = if (startAt == null) null else chatRepo.get(startAt)
            // Retrieve the authenticated user
            val owner = userRepo.getByUsername(username)

            // Configure cache control to improve performance
            val cacheControl = defaultCacheControl
            // Get the list of chats for the user
            val chatList = chatOperations.listChat(owner, atVersion, chat)
            // Transform each chat into a snapshot at the specified version
            val snapshotList = chatList.map { chat ->
                  chatOperations.getSnapshot(chat, owner, atVersion)
            }

            // Return the list of snapshots with appropriate cache headers
            return ResponseEntity.ok().cacheControl(cacheControl)
                  .varyBy("Cookie", "Authorization")
                  .body(snapshotList)
      }
}