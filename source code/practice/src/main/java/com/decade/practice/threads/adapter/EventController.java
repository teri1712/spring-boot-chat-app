package com.decade.practice.threads.adapter;

import com.decade.practice.threads.application.ports.out.EventService;
import com.decade.practice.threads.dto.EventResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
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

    @GetMapping("/chats/{chatId}/events")
    public List<EventResponse> listEvents(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable @Validated String chatId,
            @RequestParam int atVersion
    ) throws EntityNotFoundException {
        return eventService.findByOwnerAndChatAndEventVersionLessThanEqual(userId, chatId, atVersion);
    }

    // TODO: Adjust client to last event
    @GetMapping("/users/me/events")
    public List<EventResponse> listEvents(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam int atVersion
    ) throws EntityNotFoundException {
        return eventService.findByOwnerAndEventVersionLessThanEqual(userId, atVersion);
    }
}