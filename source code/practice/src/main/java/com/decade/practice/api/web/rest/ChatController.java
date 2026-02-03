package com.decade.practice.api.web.rest;

import com.decade.practice.dto.ChatDetailsDto;
import com.decade.practice.dto.ChatSnapshot;
import com.decade.practice.dto.EventRequest;
import com.decade.practice.dto.PreferenceRequest;
import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.application.usecases.EventFactoryResolution;
import com.decade.practice.persistence.jpa.embeddables.ChatIdentifier;
import com.decade.practice.persistence.jpa.entities.Chat;
import com.decade.practice.persistence.jpa.entities.PreferenceEvent;
import jakarta.validation.Valid;
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
    private final DeliveryService deliveryService;

    @GetMapping("/{identifier}")
    public ChatDetailsDto getChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable ChatIdentifier identifier
    ) {
        return chatService.getDetails(
                identifier,
                userId
        );
    }

    //TODO: adjust client to put
    @PutMapping("/{identifier}/preference")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPreference(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable ChatIdentifier identifier,
            @RequestHeader("Idempotency-key") UUID key,
            @Valid @RequestBody PreferenceRequest preference) {

        EventRequest eventRequest = new EventRequest();
        eventRequest.setSender(userId);
        eventRequest.setPreferenceEvent(preference);
        eventRequest.setChatIdentifier(identifier);
        deliveryService.createAndSend(key, eventRequest, factoryResolution.getFactory(PreferenceEvent.class));
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