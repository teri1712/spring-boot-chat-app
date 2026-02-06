package com.decade.practice.api.web.rest;

import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.dto.*;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
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


    @PostMapping(path = "/chats/{chatIdentifier}/text-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    // TODO: reimplement client to get openid
    public EventDetails createTextEvent(
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @PathVariable ChatIdentifier chatIdentifier,
            @RequestBody @Valid TextEventRequest textEventRequest) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setTextEvent(textEventRequest);
        return deliveryService.createAndSend(senderId, chatIdentifier, key, eventRequest);
    }

    @PostMapping(path = "/chats/{chatIdentifier}/image-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetails createImageEvent(
            @PathVariable ChatIdentifier chatIdentifier,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid ImageEventRequest imageEventRequest) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setImageEvent(imageEventRequest);
        return deliveryService.createAndSend(senderId, chatIdentifier, key, eventRequest);
    }

    @PostMapping(path = "/chats/{chatIdentifier}/icon-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetails createIconEvent(
            @PathVariable ChatIdentifier chatIdentifier,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid IconEventRequest iconEventRequest) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setIconEvent(iconEventRequest);
        return deliveryService.createAndSend(senderId, chatIdentifier, key, eventRequest);

    }

    @PostMapping(path = "/chats/{chatIdentifier}/file-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetails createFileEvent(
            @PathVariable ChatIdentifier chatIdentifier,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid FileEventRequest fileEventRequest) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setFileEvent(fileEventRequest);
        return deliveryService.createAndSend(senderId, chatIdentifier, key, eventRequest);

    }

    @PostMapping(path = "/chats/{chatIdentifier}/seen-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDetails createSeenEvent(
            @PathVariable ChatIdentifier chatIdentifier,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid SeenEventRequest seenEventRequest) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setSeenEvent(seenEventRequest);
        return deliveryService.createAndSend(senderId, chatIdentifier, key, eventRequest);
    }

    // TODO: Migrate real time dto != api dto, eventdto must exclude partner, owner
    @GetMapping("/chats/{chatIdentifier}/events")
    public List<EventResponse> listEvents(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Validated ChatIdentifier chatIdentifier,
            @RequestParam int atVersion
    ) throws EntityNotFoundException {
        return eventService.findByOwnerAndChatAndEventVersionLessThanEqual(userId, chatIdentifier, atVersion);
    }

    @GetMapping("/users/me/events")
    public List<EventResponse> listEvents(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam int atVersion
    ) throws EntityNotFoundException {
        return eventService.findByOwnerAndEventVersionLessThanEqual(userId, atVersion);
    }
}