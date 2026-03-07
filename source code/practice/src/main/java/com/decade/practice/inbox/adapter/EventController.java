package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.MessageAlreadySentException;
import com.decade.practice.inbox.application.query.ChatEventService;
import com.decade.practice.inbox.application.services.*;
import com.decade.practice.inbox.domain.SeenRequest;
import com.decade.practice.inbox.domain.TextRequest;
import com.decade.practice.inbox.dto.ChatEventResponse;
import com.decade.practice.inbox.dto.FileRequest;
import com.decade.practice.inbox.dto.IconRequest;
import com.decade.practice.inbox.dto.ImageRequest;
import com.decade.practice.inbox.dto.mapper.CommandMappers;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class EventController {

      private final ChatEventService chatEventService;
      private final CommandMappers commandMappers;
      private final TextPlacement textPlacement;
      private final ImagePlacement imagePlacement;
      private final IconPlacement iconPlacement;
      private final FilePlacement filePlacement;
      private final SeenPlacement seenPlacement;

      @ExceptionHandler(MessageAlreadySentException.class)
      public ChatEventResponse handle(MessageAlreadySentException e) {
            UUID idempotentKey = e.getIdempotentId();
            log.debug("Concurrent insert encountered for receipt: {}", idempotentKey, e);
            return chatEventService.find(idempotentKey);
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