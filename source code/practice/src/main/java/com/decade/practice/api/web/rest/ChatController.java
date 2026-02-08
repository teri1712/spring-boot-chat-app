package com.decade.practice.api.web.rest;

import com.decade.practice.application.services.GroupCreateChatCommandHandler;
import com.decade.practice.application.services.TwoParticipantCreateChatCommandHandler;
import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.dto.ChatDetails;
import com.decade.practice.dto.ChatSnapshot;
import com.decade.practice.dto.PreferenceCreateRequest;
import com.decade.practice.dto.mapper.EventCommandMapper;
import com.decade.practice.persistence.jpa.entities.Chat;
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
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final GroupCreateChatCommandHandler groupCreateChatStrategy;
    private final TwoParticipantCreateChatCommandHandler twoParticipantCreateChatStrategy;
    private final DeliveryService deliveryService;
    private final EventCommandMapper eventCommandMapper;

    @GetMapping("/{identifier}")
    public ChatDetails getChatDetails(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable String identifier
    ) {
        return chatService.getDetails(identifier, userId);
    }

    @PutMapping("/me/chats")
    public ChatDetails createChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam UUID partnerId
    ) {
        return twoParticipantCreateChatStrategy.create(userId, partnerId);
    }

    @PostMapping("/me/chats/groups")
    public ChatDetails createGroupChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam UUID partnerId
    ) {
        return groupCreateChatStrategy.create(userId, partnerId);
    }

    // TODO: Patch
    @PutMapping("/chats/{identifier}/preference")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPreference(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable String identifier,
            @RequestHeader("Idempotency-key") UUID key,
            @Valid @RequestBody PreferenceCreateRequest preference) {

        deliveryService.createAndSend(userId, identifier, key, eventCommandMapper.toPreference(preference, identifier, userId));
    }


    // TODO: Adjust client and test
    @GetMapping("/me/chats")
    public List<ChatSnapshot> listChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam Optional<String> startAt,
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