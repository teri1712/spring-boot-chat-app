package com.decade.practice.engagement.adapter;

import com.decade.practice.engagement.application.exceptions.ChatIdentifierUniqueException;
import com.decade.practice.engagement.application.exceptions.MessageAlreadySentException;
import com.decade.practice.engagement.application.ports.in.EngagementService;
import com.decade.practice.engagement.application.ports.in.ParticipantPlacement;
import com.decade.practice.engagement.application.query.ChatService;
import com.decade.practice.engagement.application.query.ReceiptService;
import com.decade.practice.engagement.application.services.CreateGroupChatCommand;
import com.decade.practice.engagement.dto.*;
import com.decade.practice.engagement.dto.mapper.CommandMappers;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final EngagementService engagementService;
    private final ReceiptService receiptService;
    private final ParticipantPlacement placement;
    private final CommandMappers commandMappers;

    @ExceptionHandler(ChatIdentifierUniqueException.class)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ChatResponse handle(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            ChatIdentifierUniqueException e) {
        String chatId = e.getId();
        log.debug("Concurrent insert encountered for chat: {}", chatId, e);
        return chatService.getDetails(chatId, userId);
    }

    @ExceptionHandler(MessageAlreadySentException.class)
    public ReceiptResponse handle(MessageAlreadySentException e) {
        UUID idempotentKey = e.getIdempotentId();
        log.debug("Concurrent insert encountered for receipt: {}", idempotentKey, e);
        return receiptService.find(idempotentKey);
    }

    @GetMapping("/chats/{identifier}")
    public ChatResponse getChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable String identifier
    ) {
        return chatService.getDetails(identifier, userId);
    }

    // TODO: Adjust client to get
    @GetMapping("/chats")
    public ResponseEntity<ChatResponse> getChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam UUID partnerId
    ) {
        ChatResponse chatResponse = engagementService.getOrCreate(userId, partnerId);
        HttpStatus status = chatResponse.freshOne() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(chatResponse);
    }

    @PostMapping("/groups")
    @ResponseStatus(HttpStatus.CREATED)
    public ChatResponse createGroupChat(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam UUID partnerId,
            @RequestParam String roomName
    ) {
        return engagementService.create(new CreateGroupChatCommand(userId, partnerId, roomName));
    }

    // TODO: Adjust client to patch
    @PatchMapping("/chats/{identifier}/preference")
    @ResponseStatus(HttpStatus.ACCEPTED)
    // TODO: Adjust client code
    public ReceiptResponse setPreference(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable String identifier,
            @RequestHeader("Idempotency-key") UUID key,
            @Valid @RequestBody PreferenceRequest body) {

        return placement.place(commandMappers.toPreference(body, key, userId, identifier));
    }


    @PostMapping(path = "/chats/{chatId}/text-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ReceiptResponse createTextEvent(
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @PathVariable String chatId,
            @RequestBody @Valid TextRequest body) {
        return placement.place(commandMappers.toText(body, key, senderId, chatId));
    }

    @PostMapping(path = "/chats/{chatId}/image-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ReceiptResponse createImageEvent(
            @PathVariable String chatId,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid ImageRequest body) {
        return placement.place(commandMappers.toImage(body, key, senderId, chatId));
    }

    @PostMapping(path = "/chats/{chatId}/icon-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ReceiptResponse createIconEvent(
            @PathVariable String chatId,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid IconRequest body) {
        return placement.place(commandMappers.toIcon(body, key, senderId, chatId));

    }

    @PostMapping(path = "/chats/{chatId}/file-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ReceiptResponse createFileEvent(
            @PathVariable String chatId,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid FileRequest body) {
        return placement.place(commandMappers.toFile(body, key, senderId, chatId));
    }

    @PostMapping(path = "/chats/{chatId}/seen-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ReceiptResponse createSeenEvent(
            @PathVariable String chatId,
            @AuthenticationPrincipal(expression = "id") UUID senderId,
            @RequestHeader("Idempotency-key") UUID key,
            @RequestBody @Valid SeenRequest body) {
        return placement.place(commandMappers.toSeen(body, key, senderId, chatId));
    }
}