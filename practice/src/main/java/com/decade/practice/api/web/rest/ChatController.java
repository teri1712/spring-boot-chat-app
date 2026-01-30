package com.decade.practice.api.web.rest;

import com.decade.practice.api.dto.ChatSnapshot;
import com.decade.practice.api.dto.EventRequest;
import com.decade.practice.api.dto.PreferenceDto;
import com.decade.practice.api.dto.PreferenceEventDto;
import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.application.usecases.EventFactoryResolution;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.PreferenceEvent;
import com.decade.practice.persistence.jpa.entities.Theme;
import com.decade.practice.persistence.jpa.repositories.ThemeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping("/chats")
@AllArgsConstructor
public class ChatController {

    private final EventFactoryResolution factoryResolution;
    private final ChatService chatService;
    private final ThemeRepository themeRepository;
    private final DeliveryService deliveryService;

    @GetMapping("/{identifier}")
    public ChatSnapshot getChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable ChatIdentifier identifier,
            @RequestParam Optional<Integer> atVersion
    ) {
        return chatService.getSnapshot(
                identifier,
                userId,
                atVersion.orElse(Integer.MAX_VALUE)
        );
    }

    @GetMapping("/partners/{partnerId}")
    public ChatSnapshot getChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID partnerId,
            @RequestParam(required = false) Integer atVersion
    ) {
        ChatIdentifier identifier = ChatIdentifier.from(partnerId, userId);
        return chatService.getSnapshot(
                identifier,
                userId,
                atVersion
        );
    }

    //TODO: adjust client to put
    @PutMapping("/{identifier}/preference")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPreference(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable ChatIdentifier identifier,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody PreferenceDto preference) {

        PreferenceEventDto event = new PreferenceEventDto(preference);
        EventRequest eventRequest = new EventRequest();
        eventRequest.setSender(userId);
        eventRequest.setPreferenceEvent(event);
        eventRequest.setChatIdentifier(identifier);
        deliveryService.createAndSend(key, eventRequest, factoryResolution.getFactory(PreferenceEvent.class));
    }

    @GetMapping("/themes")
    public List<Theme> getThemes() {
        return themeRepository.findAll();
    }

    @GetMapping
    public List<ChatSnapshot> listChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam Optional<ChatIdentifier> startAt,
            @RequestParam int atVersion
    ) {
        List<Chat> chatList = chatService.listChat(userId, atVersion, startAt, 20);
        return chatList.stream().map(new Function<Chat, ChatSnapshot>() {
            @Override
            public ChatSnapshot apply(Chat chat) {
                return chatService.getSnapshot(chat.getIdentifier(), userId, atVersion);
            }
        }).toList();
    }
}