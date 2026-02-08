package com.decade.practice.api.web.rest;

import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.dto.*;
import com.decade.practice.dto.mapper.EventCommandMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@AllArgsConstructor
// TODO: refactor client
public class EventController {

    private final EventService eventService;
    private final DeliveryService deliveryService;
    private final EventCommandMapper eventCommandMapper;


    @PostMapping(path = "/chats/{chatId}/text-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    // TODO: reimplement client to get openid
    public EventDetails createTextEvent(
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @PathVariable String chatId,
            @RequestBody @Valid TextEventRequest textEventRequest) {
        return deliveryService.createAndSend(senderId, chatId, key, eventCommandMapper.toText(textEventRequest, chatId, senderId));
    }

    @PostMapping(path = "/chats/{chatId}/image-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetails createImageEvent(
            @PathVariable String chatId,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid ImageEventCreateRequest imageEventCreateRequest) {
        return deliveryService.createAndSend(senderId, chatId, key, eventCommandMapper.toImage(imageEventCreateRequest, chatId, senderId));
    }

    @PostMapping(path = "/chats/{chatId}/icon-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetails createIconEvent(
            @PathVariable String chatId,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid IconEventCreateRequest iconEventCreateRequest) {
        return deliveryService.createAndSend(senderId, chatId, key, eventCommandMapper.toIcon(iconEventCreateRequest, chatId, senderId));

    }

    @PostMapping(path = "/chats/{chatId}/file-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetails createFileEvent(
            @PathVariable String chatId,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid FileEventCreateRequest fileEventCreateRequest) {
        return deliveryService.createAndSend(senderId, chatId, key, eventCommandMapper.toFile(fileEventCreateRequest, chatId, senderId));

    }

    @PostMapping(path = "/chats/{chatId}/seen-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetails createSeenEvent(
            @PathVariable String chatId,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid SeenEventCreateRequest seenEventCreateRequest) {
        return deliveryService.createAndSend(senderId, chatId, key, eventCommandMapper.toSeen(seenEventCreateRequest, chatId, senderId));
    }

    // TODO: Migrate real time dto != api dto, eventdto must exclude withPartner, owner
    @GetMapping("/chats/{chatId}/events")
    public List<EventResponse> listEvents(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Validated String chatId,
            @RequestParam int atVersion
    ) throws EntityNotFoundException {
        return eventService.findByOwnerAndChatAndEventVersionLessThanEqual(userId, chatId, atVersion);
    }

    @GetMapping("/users/me/events")
    public List<EventResponse> listEvents(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam int atVersion
    ) throws EntityNotFoundException {
        return eventService.findByOwnerAndEventVersionLessThanEqual(userId, atVersion);
    }
}