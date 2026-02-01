package com.decade.practice.api.web.rest;

import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.application.usecases.EventFactoryResolution;
import com.decade.practice.application.usecases.EventService;
import com.decade.practice.dto.*;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.*;
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
    private final EventFactoryResolution factoryResolution;


    @PostMapping(path = "/chats/{chatIdentifier}/text-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    // TODO: reimplement client to get openid
    public EventDto createTextEvent(
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @PathVariable ChatIdentifier chatIdentifier,
            @RequestBody @Valid TextEventDto textEventDto) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setChatIdentifier(chatIdentifier);
        eventRequest.setSender(senderId);
        eventRequest.setTextEvent(textEventDto);
        return deliveryService.createAndSend(key, eventRequest, factoryResolution.getFactory(TextEvent.class));
    }

    @PostMapping(path = "/chats/{chatIdentifier}/image-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createImageEvent(
            @PathVariable ChatIdentifier chatIdentifier,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid ImageEventDto imageEventDto) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setChatIdentifier(chatIdentifier);
        eventRequest.setSender(senderId);
        eventRequest.setImageEvent(imageEventDto);
        return deliveryService.createAndSend(key, eventRequest, factoryResolution.getFactory(ImageEvent.class));
    }

    @PostMapping(path = "/chats/{chatIdentifier}/icon-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createIconEvent(
            @PathVariable ChatIdentifier chatIdentifier,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid IconEventDto iconEventDto) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setChatIdentifier(chatIdentifier);
        eventRequest.setSender(senderId);
        eventRequest.setIconEvent(iconEventDto);
        return deliveryService.createAndSend(key, eventRequest, factoryResolution.getFactory(IconEvent.class));
    }

    @PostMapping(path = "/chats/{chatIdentifier}/file-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createFileEvent(
            @PathVariable ChatIdentifier chatIdentifier,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid FileEventDto fileEventDto) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setChatIdentifier(chatIdentifier);
        eventRequest.setSender(senderId);
        eventRequest.setFileEvent(fileEventDto);
        return deliveryService.createAndSend(key, eventRequest, factoryResolution.getFactory(FileEvent.class));
    }

    @PostMapping(path = "/chats/{chatIdentifier}/seen-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createSeenEvent(
            @PathVariable ChatIdentifier chatIdentifier,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid SeenEventDto seenEventDto) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setChatIdentifier(chatIdentifier);
        eventRequest.setSender(senderId);
        eventRequest.setSeenEvent(seenEventDto);
        return deliveryService.createAndSend(key, eventRequest, factoryResolution.getFactory(SeenEvent.class));
    }

    @GetMapping("/chats/{chatIdentifier}/events")
    public List<EventDto> listEvents(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Validated ChatIdentifier chatIdentifier,
            @RequestParam int atVersion
    ) throws EntityNotFoundException {
        return eventService.findByOwnerAndChatAndEventVersionLessThanEqual(userId, chatIdentifier, atVersion);
    }

    @GetMapping("/users/me/events")
    public List<EventDto> listEvents(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam int atVersion
    ) throws EntityNotFoundException {
        return eventService.findByOwnerAndEventVersionLessThanEqual(userId, atVersion);
    }
}