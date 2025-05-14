package com.decade.practice.controllers.rest

import com.decade.practice.core.ChatOperations
import com.decade.practice.core.OnlineStatistic
import com.decade.practice.database.repository.EventRepository
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.ChatEventStore
import com.decade.practice.image.ImageStore
import com.decade.practice.model.domain.embeddable.ChatIdentifier
import com.decade.practice.model.domain.embeddable.ImageSpec.Companion.DEFAULT_HEIGHT
import com.decade.practice.model.domain.embeddable.ImageSpec.Companion.DEFAULT_WIDTH
import com.decade.practice.model.domain.entity.*
import com.decade.practice.utils.CacheUtils.defaultCacheControl
import com.decade.practice.utils.EventPageUtils.pageEvent
import com.decade.practice.utils.ImageUtils
import com.decade.practice.utils.inspectOwner
import com.decade.practice.websocket.MQ_DESTINATION
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.Valid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI
import kotlin.math.min

/**
 * REST controller for managing chat events in the messaging system.
 * Provides endpoints for pushing different types of events (seen status, text messages,
 * icons, images) and retrieving events for chats.
 */
@RestController
@RequestMapping("/message")
class EventController(
      private val chatEventStore: ChatEventStore,
      private val template: SimpMessagingTemplate,
      private val chatOperations: ChatOperations,
      private val evenRepo: EventRepository,
      private val onlineStat: OnlineStatistic,
      private val userRepo: UserRepository,
      private val imageStore: ImageStore,
) {

      /**
       * Generic method to process and distribute chat events.
       * Updates the user's online status, saves the event, and pushes it to recipients via WebSocket.
       *
       * @param sender The user sending the event
       * @param event The chat event to be processed
       * @return The saved event with the same local ID
       * @throws EntityNotFoundException If the user or other required entities aren't found
       */
      @Throws(EntityNotFoundException::class)
      private fun <E : ChatEvent> pushEvent(
            sender: User,
            @RequestBody event: E
      ): E {
            onlineStat.set(sender)
            event.sender = sender
            try {
                  val saved = chatEventStore.save(event)
                  saved.forEach {
                        template.convertAndSendToUser(
                              it.owner.username,
                              MQ_DESTINATION,
                              it
                        )
                  }
                  return saved.find {
                        it.localId == event.localId
                  } as E
            } catch (e: DataIntegrityViolationException) {
                  // record already sent
                  return evenRepo.getByLocalId(event.localId) as E
            }
      }

      /**
       * Endpoint for pushing "seen" events.
       * Marks messages as seen by a user.
       *
       * @param username The authenticated user's username
       * @param record The seen event data
       * @return The saved seen event
       * @throws EntityNotFoundException If the user isn't found
       */
      @PostMapping("/seen")
      @Throws(EntityNotFoundException::class)
      fun pushEvent(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestBody @Valid record: SeenEvent
      ): SeenEvent = pushEvent(userRepo.getByUsername(username), record)

      /**
       * Endpoint for pushing text message events.
       * Sends a text message from one user to another.
       *
       * @param username The authenticated user's username
       * @param event The text event containing the message
       * @return The saved text event
       * @throws EntityNotFoundException If the user isn't found
       */
      @PostMapping("/text")
      @Throws(EntityNotFoundException::class)
      fun pushEvent(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestBody @Valid event: TextEvent
      ): TextEvent = pushEvent(userRepo.getByUsername(username), event)

      /**
       * Endpoint for pushing icon events.
       * Sends an icon/emoji from one user to another.
       *
       * @param username The authenticated user's username
       * @param event The icon event data
       * @return The saved icon event
       * @throws EntityNotFoundException If the user isn't found
       */
      @PostMapping("/icon")
      @Throws(EntityNotFoundException::class)
      fun pushEvent(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestBody @Valid event: IconEvent
      ): IconEvent = pushEvent(userRepo.getByUsername(username), event)

      /**
       * Endpoint for pushing image events with file upload.
       * Processes the uploaded image file, stores it, and sends an image event.
       *
       * @param username The authenticated user's username
       * @param event The image event metadata
       * @param file The image file being uploaded
       * @return The saved image event
       * @throws EntityNotFoundException If the user isn't found
       * @throws IOException If there's an error processing the image file
       */
      @PostMapping("/image")
      @Throws(EntityNotFoundException::class, IOException::class)
      fun pushEvent(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestPart("event") @Valid event: ImageEvent,
            @RequestPart("file") file: MultipartFile
      ): ImageEvent {
            val maxWidth = min(event.image.width, DEFAULT_WIDTH)
            val maxHeight = min(event.image.height, DEFAULT_HEIGHT)
            val image = ImageUtils.crop(file.inputStream, maxWidth, maxHeight)

            event.image = imageStore.save(image)
            try {
                  return pushEvent(userRepo.getByUsername(username), event)
            } catch (e: Exception) {
                  imageStore.remove(URI(event.image.uri))
                  throw e
            }
      }

      /**
       * Retrieves events for a specific chat up to a given version number.
       * Uses cache control headers for efficient client-side caching.
       *
       * @param username The authenticated user's username
       * @param identifier The chat identifier (participants)
       * @param atVersion The maximum event version to retrieve
       * @return A list of chat events for the specified chat
       * @throws EntityNotFoundException If the user or chat isn't found
       */
      @GetMapping("/chat")
      @Throws(EntityNotFoundException::class)
      fun pullEvents(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestParam @Validated identifier: ChatIdentifier,
            @RequestParam atVersion: Int
      ): ResponseEntity<List<ChatEvent>> {

            val chat = chatOperations.getOrCreateChat(identifier)
            val user = chat.inspectOwner(username)
            val cacheControl = defaultCacheControl
            val events = evenRepo.findByOwnerAndChatAndEventVersionLessThanEqual(user, chat, atVersion, pageEvent)
            return ResponseEntity.ok()
                  .varyBy("Cookie", "Authorization")
                  .cacheControl(cacheControl)
                  .body(events)
      }

      /**
       * Retrieves all events for a user up to a given version number.
       * Fetches events across all chats the user participates in.
       *
       * @param username The authenticated user's username
       * @param atVersion The maximum event version to retrieve
       * @return A list of chat events for the user
       * @throws EntityNotFoundException If the user isn't found
       */
      @GetMapping
      @Throws(EntityNotFoundException::class)
      fun pullEvents(
            @AuthenticationPrincipal(expression = "name") username: String,
            @RequestParam atVersion: Int
      ): ResponseEntity<List<ChatEvent>> {

            val owner = userRepo.getByUsername(username)
            val cacheControl = defaultCacheControl
            val eventList = evenRepo.findByOwnerAndEventVersionLessThanEqual(owner, atVersion, pageEvent)
            return ResponseEntity.ok()
                  .varyBy("Cookie", "Authorization")
                  .cacheControl(cacheControl)
                  .body(eventList)
      }
}