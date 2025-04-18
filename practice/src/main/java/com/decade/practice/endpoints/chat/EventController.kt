package com.decade.practice.endpoints.chat

import com.decade.practice.database.ChatOperations
import com.decade.practice.database.repository.EventRepository
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.repository.get
import com.decade.practice.database.transaction.ChatEventStore
import com.decade.practice.image.ImageStore
import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.embeddable.ImageSpec.Companion.DEFAULT_HEIGHT
import com.decade.practice.model.embeddable.ImageSpec.Companion.DEFAULT_WIDTH
import com.decade.practice.model.entity.*
import com.decade.practice.util.EventPageUtils
import com.decade.practice.util.ImageUtils
import com.decade.practice.util.inspectOwner
import com.decade.practice.websocket.MQ_DESTINATION
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.CacheControl
import org.springframework.http.ResponseEntity
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.min


@RestController
@RequestMapping("/message")
class EventController(
    private val chatEventStore: ChatEventStore,
    private val template: SimpMessagingTemplate,
    private val chatOperations: ChatOperations,
    private val evenRepo: EventRepository,
    private val userRepo: UserRepository,
    private val imageStore: ImageStore,
) {

    @Throws(EntityNotFoundException::class)
    private fun <E : ChatEvent> pushEvent(
        sender: User,
        @RequestBody event: E
    ): E {
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

    @PostMapping("/seen")
    @Throws(EntityNotFoundException::class)
    fun pushEvent(
        @AuthenticationPrincipal(expression = "id") sender: UUID,
        @RequestBody @Valid record: SeenEvent
    ): SeenEvent {
        return pushEvent(userRepo.get(sender), record)
    }

    @PostMapping("/text")
    @Throws(EntityNotFoundException::class)
    fun pushEvent(
        @AuthenticationPrincipal(expression = "id") sender: UUID,
        @RequestBody @Valid event: TextEvent
    ): TextEvent {
        return pushEvent(userRepo.get(sender), event)
    }

    @PostMapping("/icon")
    @Throws(EntityNotFoundException::class)
    fun pushEvent(
        @AuthenticationPrincipal(expression = "id") sender: UUID,
        @RequestBody @Valid event: IconEvent
    ): IconEvent {
        return pushEvent(userRepo.get(sender), event)
    }

    @PostMapping("/image")
    @Throws(EntityNotFoundException::class, IOException::class)
    fun pushEvent(
        httpRequest: HttpServletRequest,
        @AuthenticationPrincipal(expression = "id") sender: UUID,
        @RequestPart("event") @Valid event: ImageEvent,
        @RequestPart("file") file: MultipartFile
    ): ImageEvent {
        val maxWidth = min(event.image.width, DEFAULT_WIDTH)
        val maxHeight = min(event.image.height, DEFAULT_HEIGHT)
        val image = ImageUtils.crop(file.inputStream, maxWidth, maxHeight)

        event.image = imageStore.save(image)
        try {
            return pushEvent(userRepo.get(sender), event)
        } catch (e: Exception) {
            imageStore.remove(URI(event.image.uri))
            throw e
        }
    }

    @GetMapping("/chat")
    @Throws(EntityNotFoundException::class)
    fun pullEvents(
        @AuthenticationPrincipal(expression = "id") id: UUID,
        @RequestParam @Validated identifier: ChatIdentifier,
        @RequestParam atVersion: Int
    ): ResponseEntity<List<ChatEvent>> {

        val chat = chatOperations.getOrCreateChat(identifier)
        val user = chat.inspectOwner(id)
        val page = EventPageUtils.pageEvent
        val cacheControl = CacheControl.maxAge(30, TimeUnit.DAYS)
            .cachePublic()
        val events = evenRepo.findByOwnerAndChatAndEventVersionLessThanEqual(user, chat, atVersion, page)
        return ResponseEntity.ok()
            .cacheControl(cacheControl)
            .body(events)
    }

    @GetMapping
    @Throws(EntityNotFoundException::class)
    fun pullEvents(
        @AuthenticationPrincipal(expression = "id") id: UUID,
        @RequestParam atVersion: Int
    ): ResponseEntity<List<ChatEvent>> {

        val owner = userRepo.get(id)
        val page = EventPageUtils.pageEvent
        val cacheControl = CacheControl.maxAge(30, TimeUnit.DAYS)
            .cachePublic()
        val eventList = evenRepo.findByOwnerAndEventVersionLessThanEqual(owner, atVersion, page)
        return ResponseEntity.ok()
            .cacheControl(cacheControl)
            .body(eventList)
    }
}
