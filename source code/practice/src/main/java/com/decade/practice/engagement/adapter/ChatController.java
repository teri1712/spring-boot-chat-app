package com.decade.practice.engagement.adapter;

import com.decade.practice.engagement.application.exceptions.ChatIdentifierUniqueException;
import com.decade.practice.engagement.application.exceptions.MessageAlreadySentException;
import com.decade.practice.engagement.application.ports.in.EngagementService;
import com.decade.practice.engagement.application.query.ChatEventService;
import com.decade.practice.engagement.application.query.ChatService;
import com.decade.practice.engagement.application.services.*;
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
      private final ChatEventService chatEventService;
      private final CommandMappers commandMappers;
      private final PreferencePlacement preferencePlacement;
      private final TextPlacement textPlacement;
      private final ImagePlacement imagePlacement;
      private final IconPlacement iconPlacement;
      private final FilePlacement filePlacement;
      private final SeenPlacement seenPlacement;

      @ExceptionHandler(ChatIdentifierUniqueException.class)
      @ResponseStatus(HttpStatus.ACCEPTED)
      public ChatResponse handle(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                ChatIdentifierUniqueException e) {
            String chatId = e.getId();
            log.debug("Concurrent insert encountered for chat: {}", chatId, e);
            return chatService.find(chatId, userId);
      }

      @ExceptionHandler(MessageAlreadySentException.class)
      public ChatEventResponse handle(MessageAlreadySentException e) {
            UUID idempotentKey = e.getIdempotentId();
            log.debug("Concurrent insert encountered for receipt: {}", idempotentKey, e);
            return chatEventService.find(idempotentKey);
      }

      @GetMapping("/chats/{identifier}")
      public ChatResponse getChat(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @PathVariable String identifier
      ) {
            return chatService.find(identifier, userId);
      }

      // TODO: Adjust client to get
      @PostMapping("/chats")
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
      public ChatEventResponse setPreference(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @PathVariable String identifier,
                @RequestHeader("Idempotency-key") UUID key,
                @Valid @RequestBody PreferenceRequest body) {

            return preferencePlacement.place(commandMappers.toPreference(body, key, userId, identifier));
      }


      @PostMapping(path = "/chats/{chatId}/text-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public ChatEventResponse createTextEvent(
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @RequestHeader("Idempotency-key") UUID key,
                @PathVariable String chatId,
                @RequestBody @Valid TextRequest body) {
            return textPlacement.place(commandMappers.toText(body, key, senderId, chatId));
      }

      @PostMapping(path = "/chats/{chatId}/image-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public ChatEventResponse createImageEvent(
                @PathVariable String chatId,
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @RequestHeader("Idempotency-key") UUID key,
                @RequestBody @Valid ImageRequest body) {
            return imagePlacement.place(commandMappers.toImage(body, key, senderId, chatId));
      }

      @PostMapping(path = "/chats/{chatId}/icon-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public ChatEventResponse createIconEvent(
                @PathVariable String chatId,
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @RequestHeader("Idempotency-key") UUID key,
                @RequestBody @Valid IconRequest body) {
            return iconPlacement.place(commandMappers.toIcon(body, key, senderId, chatId));

      }

      @PostMapping(path = "/chats/{chatId}/file-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public ChatEventResponse createFileEvent(
                @PathVariable String chatId,
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @RequestHeader("Idempotency-key") UUID key,
                @RequestBody @Valid FileRequest body) {
            return filePlacement.place(commandMappers.toFile(body, key, senderId, chatId));
      }

      @PostMapping(path = "/chats/{chatId}/seen-events", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public ChatEventResponse createSeenEvent(
                @PathVariable String chatId,
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @RequestHeader("Idempotency-key") UUID key,
                @RequestBody @Valid SeenRequest body) {
            return seenPlacement.place(commandMappers.toSeen(body, key, senderId, chatId));
      }
}